package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.visitor.StartSectionVisitor

public class StartSectionVerifier(
    private val delegate: StartSectionVisitor? = null,
    private val context: VerifierContext,
    private val functionIndex: UInt,
) : StartSectionVisitor {
    private var done: Boolean = false

    override fun visitEnd() {
        checkEnd()

        if (functionIndex >= context.numberOfTotalFunctions) {
            throw ParserException("Invalid start function index: %$functionIndex")
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
