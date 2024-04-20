package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.sections.ExceptionType
import org.wasmium.wasm.binary.visitors.ExceptionSectionVisitor

public class ExceptionSectionValidator(private val delegate: ExceptionSectionVisitor? = null, private val context: ValidatorContext) : ExceptionSectionVisitor {
    override fun visitExceptionType(types: List<WasmType>) {
        context.exceptions.add(ExceptionType(types))

        delegate?.visitExceptionType(types)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
