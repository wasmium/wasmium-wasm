package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.ExternalKind.FUNCTION
import org.wasmium.wasm.binary.tree.ExternalKind.GLOBAL
import org.wasmium.wasm.binary.tree.ExternalKind.MEMORY
import org.wasmium.wasm.binary.tree.ExternalKind.TABLE
import org.wasmium.wasm.binary.tree.ExternalKind.TAG
import org.wasmium.wasm.binary.visitor.ExportSectionVisitor

public class ExportSectionVerifier(private val delegate: ExportSectionVisitor? = null, private val context: VerifierContext) : ExportSectionVisitor {
    private var done: Boolean = false
    private var numberOfExports = 0u
    private val names = mutableSetOf<String>()

    override fun visitExport(name: String, externalKind: ExternalKind, itemIndex: UInt) {
        checkEnd()

        if (!names.add(name)) {
            throw ParserException("Duplicate export name $name")
        }

        when (externalKind) {
            FUNCTION -> {
                if (itemIndex >= context.numberOfTotalFunctions) {
                    throw ParserException("Invalid export function index: %$itemIndex")
                }
            }

            TABLE -> {
                if (context.numberOfTotalTables == 0u) {
                    throw ParserException("Cannot index non existing table")
                }

                if (itemIndex != 0u) {
                    throw ParserException("Only table index 0 is supported, but using index $itemIndex")
                }

                if (itemIndex > context.numberOfTotalTables) {
                    throw ParserException("Invalid export table index: %$itemIndex")
                }
            }

            MEMORY -> {
                if (context.numberOfTotalMemories == 0u) {
                    throw ParserException("Cannot index non existing memory")
                }

                if (itemIndex != 0u) {
                    throw ParserException("Only memory index 0 is supported, but using index $itemIndex")
                }

                if (itemIndex > context.numberOfTotalMemories) {
                    throw ParserException("Invalid export memories index: %$itemIndex")
                }
            }

            GLOBAL -> {
                if (itemIndex >= context.numberOfTotalGlobals) {
                    throw ParserException("Invalid export global index: %$itemIndex")
                }
            }

            TAG -> {
                context.exportIndexes.add(itemIndex)
            }
        }

        numberOfExports++

        delegate?.visitExport(name, externalKind, itemIndex)
    }

    override fun visitEnd() {
        checkEnd()

        if (this.numberOfExports > WasmBinary.MAX_EXPORTS) {
            throw VerifierException("Number of exports $numberOfExports exceed the maximum of ${WasmBinary.MAX_EXPORTS}")
        }

        done = true
        delegate?.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
