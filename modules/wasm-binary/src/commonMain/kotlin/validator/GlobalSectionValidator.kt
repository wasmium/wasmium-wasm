package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExpressionVisitor
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor

public class GlobalSectionValidator(private val delegate: GlobalSectionVisitor, private val context: ValidatorContext) : GlobalSectionVisitor {
    override fun visitGlobalVariable(type: WasmType, mutable: Boolean): ExpressionVisitor {
        return delegate.visitGlobalVariable(type, mutable)
    }

    override fun visitEnd() {
        delegate.visitEnd()
    }
}
