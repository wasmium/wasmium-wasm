package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.sections.TypeSignatureNode
import org.wasmium.wasm.binary.visitors.TypeSectionVisitor

public class TypeSectionVerifier(private val delegate: TypeSectionVisitor, private val context: VerifierContext) : TypeSectionVisitor {
    private var done: Boolean = false
    private var numberOfSignatures: UInt = 0u

    override fun visitType(parameters: List<WasmType>, results: List<WasmType>) {
        checkEnd()

        numberOfSignatures++

        context.typeSignatures.add(TypeSignatureNode(parameters, results))

        delegate.visitType(parameters, results)
    }

    override fun visitEnd() {
        checkEnd()

        if (this.numberOfSignatures > WasmBinary.MAX_TYPES) {
            throw VerifierException("Number of types $numberOfSignatures exceed the maximum of ${WasmBinary.MAX_TYPES}");
        }

        context.numberOfSignatures = numberOfSignatures

        done = true
        delegate.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
