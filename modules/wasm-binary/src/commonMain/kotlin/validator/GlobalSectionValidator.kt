package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.sections.GlobalType
import org.wasmium.wasm.binary.visitors.ExpressionVisitor
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor

public class GlobalSectionValidator(private val delegate: GlobalSectionVisitor? = null, private val context: ValidatorContext) : GlobalSectionVisitor {
    override fun visitGlobalVariable(type: WasmType, mutable: Boolean): ExpressionVisitor {
        context.globals.add(GlobalType(type, mutable))

        return ExpressionValidator(delegate?.visitGlobalVariable(type, mutable), context)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
