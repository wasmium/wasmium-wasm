package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExceptionSectionVisitor

public class ExceptionSectionVerifier(private val delegate: ExceptionSectionVisitor? = null, private val context: VerifierContext) : ExceptionSectionVisitor {
    private var done: Boolean = false

    override fun visitExceptionType(types: List<WasmType>) {
        checkEnd()

        if (types.size.toUInt() > WasmBinary.MAX_EXCEPTION_TYPES) {
            throw VerifierException("Number of exception types ${types.size} exceed the maximum of ${WasmBinary.MAX_EXCEPTION_TYPES}")
        }
        context.numberOfExceptions++

        delegate?.visitExceptionType(types)
    }

    override fun visitEnd() {
        checkEnd()

        if (context.numberOfExceptions > WasmBinary.MAX_EXCEPTIONS) {
            throw VerifierException("Number of exceptions ${context.numberOfExceptions} exceed the maximum of ${WasmBinary.MAX_EXCEPTIONS}")
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
