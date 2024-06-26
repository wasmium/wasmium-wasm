package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.verifier.VerifierException
import org.wasmium.wasm.binary.visitor.StartSectionVisitor

public class StartSectionValidator(private val delegate: StartSectionVisitor? = null, private val context: ValidatorContext, private val functionIndex: UInt) :
    StartSectionVisitor {
    override fun visitEnd() {
        val type = context.functions.getOrElse(functionIndex.toInt()) {
            throw VerifierException("Invalid start function index $functionIndex")
        }

        if (type.parameters.isNotEmpty()) {
            throw VerifierException("Start function can't have arguments")
        }

        if (type.results.isNotEmpty() && (type.results.size == 1) && (type.results.first() == WasmType.NONE)) {
            throw VerifierException("Start function can't return a value")
        }

        delegate?.visitEnd()
    }
}
