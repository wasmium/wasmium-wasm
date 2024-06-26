package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.FunctionType
import org.wasmium.wasm.binary.visitor.FunctionSectionVisitor

public class FunctionSectionVerifier(private val delegate: FunctionSectionVisitor? = null, private val context: VerifierContext) : FunctionSectionVisitor {
    private var done: Boolean = false
    private var numberOfFunctions: UInt = 0u

    override fun visitFunction(functionType: FunctionType) {
        checkEnd()

        numberOfFunctions++

        delegate?.visitFunction(functionType)
    }

    override fun visitEnd() {
        checkEnd()

        if (this.numberOfFunctions > WasmBinary.MAX_FUNCTIONS) {
            throw VerifierException("Number of functions $numberOfFunctions exceed the maximum of ${WasmBinary.MAX_FUNCTIONS}");
        }

        context.numberOfFunctions = numberOfFunctions

        done = true
        delegate?.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
