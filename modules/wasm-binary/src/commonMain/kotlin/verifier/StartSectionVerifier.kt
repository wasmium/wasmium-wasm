package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.StartSectionVisitor

public class StartSectionVerifier(
    private val delegate: StartSectionVisitor,
    private val context: VerifierContext,
    private val functionIndex: UInt,
) : StartSectionVisitor {
    private var done: Boolean = false

    override fun visitEnd() {
        checkEnd()

        val startSignature = context.typeSignatures.getOrNull(functionIndex.toInt()) ?: throw VerifierException("Start functionIndex not found")

        if (startSignature.parameters.isNotEmpty()) {
            throw VerifierException("Start function can't have arguments")
        }

        if (startSignature.results.isNotEmpty() || startSignature.results.first() != WasmType.NONE) {
            throw VerifierException("Start function can't return a value")
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
