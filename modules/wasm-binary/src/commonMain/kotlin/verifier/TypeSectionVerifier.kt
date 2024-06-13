package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.FunctionType
import org.wasmium.wasm.binary.visitor.TypeSectionVisitor

public class TypeSectionVerifier(private val delegate: TypeSectionVisitor? = null, private val context: VerifierContext) : TypeSectionVisitor {
    private var done: Boolean = false

    override fun visitType(functionType: FunctionType) {
        checkEnd()

        if (functionType.parameters.size.toUInt() > WasmBinary.MAX_FUNCTION_PARAMETERS) {
            throw ParserException("Number of function parameters ${functionType.parameters.size.toUInt()} exceed the maximum of ${WasmBinary.MAX_FUNCTION_PARAMETERS}")
        }

        if (functionType.results.size.toUInt() > WasmBinary.MAX_FUNCTION_RESULTS) {
            throw ParserException("Number of function results ${functionType.results.size.toUInt()} exceed the maximum of ${WasmBinary.MAX_FUNCTION_RESULTS}")
        }

        context.numberOfTypes++

        delegate?.visitType(functionType)
    }

    override fun visitEnd() {
        checkEnd()

        if (context.numberOfTypes > WasmBinary.MAX_TYPES) {
            throw VerifierException("Number of types ${context.numberOfTypes} exceed the maximum of ${WasmBinary.MAX_TYPES}");
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
