package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.ExternalKind.EXCEPTION
import org.wasmium.wasm.binary.tree.ExternalKind.GLOBAL
import org.wasmium.wasm.binary.visitors.ExportSectionVisitor

public class ExportSectionVerifier(private val delegate: ExportSectionVisitor, private val context: VerifierContext) : ExportSectionVisitor {
    private var done: Boolean = false
    private var numberOfExports = 0u

    override fun visitExport(name: String, externalKind: ExternalKind, itemIndex: UInt) {
        checkEnd()

        when (externalKind) {
            GLOBAL -> {
                // cannot export mutable globals
                if (context.mutableGlobals.get(itemIndex.toInt())) {
                    throw VerifierException("Invalid export global of mutable index: %$itemIndex")
                }
            }

            EXCEPTION -> {
                // save exceptions indexes to validate at the end of the module
                context.exportIndexes.add(itemIndex)
            }

            else -> {
                // empty
            }
        }

        numberOfExports++

        delegate.visitExport(name, externalKind, itemIndex)
    }

    override fun visitEnd() {
        checkEnd()

        if (this.numberOfExports > WasmBinary.MAX_EXPORTS) {
            throw VerifierException("Number of exports $numberOfExports exceed the maximum of ${WasmBinary.MAX_EXPORTS}")
        }

        done = true
        delegate.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
