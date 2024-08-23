package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.visitor.ExternalDebugSectionVisitor

public class ExternalDebugSectionVerifier(
    private val delegate: ExternalDebugSectionVisitor? = null,
    private val context: VerifierContext,
) : ExternalDebugSectionVisitor {
    private var done: Boolean = false

    override fun visitEnd() {
        checkEnd()

        done = true
        delegate?.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
