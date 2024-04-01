package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class GlobalSectionVerifier(private val delegate: GlobalSectionVisitor, private val context: VerifierContext) : GlobalSectionVisitor {
    private var done: Boolean = false
    private var numberOfGlobals: UInt = 0u

    override fun visitGlobalVariable(type: WasmType, mutable: Boolean): InitializerExpressionVisitor {
        checkEnd()

        numberOfGlobals++

        context.mutableGlobals.add(mutable)

        return InitializerExpressionVerifier(delegate.visitGlobalVariable(type, mutable), context)
    }

    override fun visitEnd() {
        checkEnd()

        if (this.numberOfGlobals > WasmBinary.MAX_GLOBALS) {
            throw VerifierException("Number of globals $numberOfGlobals exceed the maximum of ${WasmBinary.MAX_GLOBALS}")
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
