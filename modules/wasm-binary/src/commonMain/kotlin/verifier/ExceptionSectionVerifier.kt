package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExceptionSectionVisitor

public class ExceptionSectionVerifier(private val delegate: ExceptionSectionVisitor, private val context: VerifierContext) : ExceptionSectionVisitor {
    private var done: Boolean = false
    private var numberOfExceptions: UInt = 0u

    override fun visitExceptionType(types: List<WasmType>) {
        checkEnd()

        numberOfExceptions++

        delegate.visitExceptionType(types)
    }

    override fun visitEnd() {
        checkEnd()

        if (this.numberOfExceptions > WasmBinary.MAX_EXCEPTIONS) {
            throw VerifierException("Number of exceptions $numberOfExceptions exceed the maximum of ${WasmBinary.MAX_EXCEPTIONS}")
        }

        context.numberOfExceptions = numberOfExceptions

        done = true
        delegate.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
