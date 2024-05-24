package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.GlobalType
import org.wasmium.wasm.binary.tree.GlobalType.Mutability
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExpressionVisitor
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor

public class GlobalSectionValidator(private val delegate: GlobalSectionVisitor? = null, private val context: ValidatorContext) : GlobalSectionVisitor {
    override fun visitGlobalVariable(type: WasmType, mutability: Mutability): ExpressionVisitor {
        context.checkGlobalType(type, mutability)

        context.globals.add(GlobalType(type, mutability))

        val localContext = context.createLocalContext(listOf(type), emptyList())
        return ConstantExpressionValidator(ExpressionValidator(delegate?.visitGlobalVariable(type, mutability), localContext, listOf(type)), context)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
