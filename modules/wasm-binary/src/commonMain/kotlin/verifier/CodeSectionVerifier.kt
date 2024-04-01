package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.LocalVariable
import org.wasmium.wasm.binary.visitors.CodeSectionVisitor
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class CodeSectionVerifier(private val delegate: CodeSectionVisitor, private val context: VerifierContext) : CodeSectionVisitor {
    private var done: Boolean = false
    private var numberOfCodes: UInt = 0u
    private var numberOfLocals: UInt = 0u

    override fun visitCode(locals: List<LocalVariable>): ExpressionVisitor {
        checkEnd()

        if (this.numberOfLocals > WasmBinary.MAX_FUNCTION_LOCALS) {
            throw VerifierException("Number of function locals $numberOfLocals exceed the maximum of ${WasmBinary.MAX_FUNCTION_LOCALS}")
        }

        numberOfCodes++

        return ExpressionVerifier(delegate.visitCode(locals), context)
    }

    override fun visitEnd() {
        checkEnd()

        if (numberOfCodes != context.numberOfFunctions) {
            throw VerifierException("Invalid function body size: $numberOfCodes must be equal to the number of functions of: ${context.numberOfFunctions}")
        }

        if (this.numberOfCodes > WasmBinary.MAX_FUNCTIONS) {
            throw VerifierException("Number of functions $numberOfCodes exceed the maximum of ${WasmBinary.MAX_FUNCTIONS}")
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
