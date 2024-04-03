package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.visitors.SourceMapSectionVisitor

public class SourceMapSectionVerifier(
    private val delegate: SourceMapSectionVisitor,
    private val context: VerifierContext,
) : SourceMapSectionVisitor {
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
