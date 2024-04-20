package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.sections.GlobalType
import org.wasmium.wasm.binary.visitors.ExpressionVisitor
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor

public class GlobalSectionVerifier(private val delegate: GlobalSectionVisitor? = null, private val context: VerifierContext) : GlobalSectionVisitor {
    private var done: Boolean = false

    override fun visitGlobalVariable(type: WasmType, mutable: Boolean): ExpressionVisitor {
        checkEnd()

        context.numberOfGlobals++

        return ExpressionVerifier(delegate?.visitGlobalVariable(type, mutable), context)
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
