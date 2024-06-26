package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.GlobalType
import org.wasmium.wasm.binary.visitor.ExpressionVisitor
import org.wasmium.wasm.binary.visitor.GlobalSectionVisitor

public class GlobalSectionVerifier(private val delegate: GlobalSectionVisitor? = null, private val context: VerifierContext) : GlobalSectionVisitor {
    private var done: Boolean = false

    override fun visitGlobalVariable(globalType: GlobalType): ExpressionVisitor {
        checkEnd()

        context.numberOfGlobals++

        return ExpressionVerifier(delegate?.visitGlobalVariable(globalType), context)
    }

    override fun visitEnd() {
        checkEnd()

        if (context.numberOfGlobals > WasmBinary.MAX_GLOBALS) {
            throw VerifierException("Number of globals ${context.numberOfGlobals} exceed the maximum of ${WasmBinary.MAX_GLOBALS}")
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
