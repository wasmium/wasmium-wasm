package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.verifier.VerifierException
import org.wasmium.wasm.binary.visitors.StartSectionVisitor

public class StartSectionValidator(private val delegate: StartSectionVisitor, private val context: ValidatorContext, private val functionIndex: UInt) : StartSectionVisitor {
    override fun visitEnd() {
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

        delegate.visitEnd()
    }
}
