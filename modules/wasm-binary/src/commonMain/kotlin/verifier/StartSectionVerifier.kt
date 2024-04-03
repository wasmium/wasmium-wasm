package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.ParserException
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

        if (functionIndex >= context.numberOfTotalFunctions) {
            throw ParserException("Invalid start function index: %$functionIndex")
        }

        val signatureIndex = context.functions.getOrElse(functionIndex.toInt()) {
            throw VerifierException("Start function index is greater than the number of functions")
        }

        val signature = context.signatures.getOrElse(signatureIndex.toInt()) {
            throw VerifierException("Invalid signature index is greater than the number of type signatures")
        }

        if (signature.parameters.isNotEmpty()) {
            throw VerifierException("Start function can't have arguments")
        }

        if (signature.results.isNotEmpty() && (signature.results.size == 1) && (signature.results.first() == WasmType.NONE)) {
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
