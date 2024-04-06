package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.sections.TypeSignature
import org.wasmium.wasm.binary.visitors.TypeSectionVisitor

public class TypeSectionVerifier(private val delegate: TypeSectionVisitor, private val context: VerifierContext) : TypeSectionVisitor {
    private var done: Boolean = false

    override fun visitType(parameters: List<WasmType>, results: List<WasmType>) {
        checkEnd()

        if (parameters.size.toUInt() > WasmBinary.MAX_FUNCTION_PARAMS) {
            throw ParserException("Number of function parameters ${parameters.size.toUInt()} exceed the maximum of ${WasmBinary.MAX_FUNCTION_PARAMS}")
        }

        if (results.size.toUInt() > WasmBinary.MAX_FUNCTION_RESULTS) {
            throw ParserException("Number of function results ${results.size.toUInt()} exceed the maximum of ${WasmBinary.MAX_FUNCTION_RESULTS}")
        }

        context.signatures.add(TypeSignature(parameters, results))

        context.numberOfSignatures++

        delegate.visitType(parameters, results)
    }

    override fun visitEnd() {
        checkEnd()

        if (context.numberOfSignatures > WasmBinary.MAX_TYPES) {
            throw VerifierException("Number of types ${context.numberOfSignatures} exceed the maximum of ${WasmBinary.MAX_TYPES}");
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
