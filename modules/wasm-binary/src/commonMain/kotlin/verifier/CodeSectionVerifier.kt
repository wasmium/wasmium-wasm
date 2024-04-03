package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.LocalVariable
import org.wasmium.wasm.binary.visitors.CodeSectionVisitor
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class CodeSectionVerifier(private val delegate: CodeSectionVisitor, private val context: VerifierContext) : CodeSectionVisitor {
    private var done: Boolean = false
    private var numberOfCodes: UInt = 0u
    private var totalNumberOfLocals: UInt = 0u

    override fun visitCode(locals: List<LocalVariable>): ExpressionVisitor {
        checkEnd()

        val numberOfLocals = locals.sumOf { it.count }
        if (numberOfLocals > WasmBinary.MAX_FUNCTION_LOCALS) {
            throw VerifierException("Number of function locals ${locals.size} exceed the maximum of ${WasmBinary.MAX_FUNCTION_LOCALS}")
        }

        numberOfCodes++
        totalNumberOfLocals += numberOfLocals

        return ExpressionVerifier(delegate.visitCode(locals), context)
    }

    override fun visitEnd() {
        checkEnd()

        if (numberOfCodes != context.numberOfFunctions) {
            throw VerifierException("Invalid code section size: $numberOfCodes must be equal to the number of functions of: ${context.numberOfFunctions}")
        }

        if (numberOfCodes > WasmBinary.MAX_FUNCTIONS) {
            throw VerifierException("Number of functions $numberOfCodes exceed the maximum of ${WasmBinary.MAX_FUNCTIONS}")
        }

        if (totalNumberOfLocals > WasmBinary.MAX_FUNCTION_LOCALS_TOTAL) {
            throw VerifierException("Number of total function locals $totalNumberOfLocals exceed the maximum of ${WasmBinary.MAX_FUNCTION_LOCALS_TOTAL}")
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
