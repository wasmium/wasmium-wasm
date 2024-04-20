package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExceptionSectionVisitor

public class ExceptionSectionValidator(private val delegate: ExceptionSectionVisitor, private val context: ValidatorContext) : ExceptionSectionVisitor {
    override fun visitExceptionType(types: List<WasmType>) {
        delegate.visitExceptionType(types)
    }

    override fun visitEnd() {
        delegate.visitEnd()
    }
}
