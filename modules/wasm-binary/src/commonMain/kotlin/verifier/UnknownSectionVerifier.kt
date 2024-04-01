package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.visitors.UnknownSectionVisitor

public class UnknownSectionVerifier(private val delegate: UnknownSectionVisitor, private val context: VerifierContext) : UnknownSectionVisitor {
    private var done: Boolean = false

    override fun visitEnd() {
        checkEnd()

        done = true
        delegate.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
