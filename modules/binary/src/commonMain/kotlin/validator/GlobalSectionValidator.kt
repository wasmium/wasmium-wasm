package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.GlobalType
import org.wasmium.wasm.binary.visitor.ExpressionVisitor
import org.wasmium.wasm.binary.visitor.GlobalSectionVisitor

public class GlobalSectionValidator(private val delegate: GlobalSectionVisitor? = null, private val context: ValidatorContext) : GlobalSectionVisitor {
    override fun visitGlobalVariable(globalType: GlobalType): ExpressionVisitor {
        context.checkGlobalType(globalType)

        context.globals.add(globalType)

        val localContext = context.createLocalContext(listOf(globalType.contentType), emptyList())
        return ConstantExpressionValidator(OperatorExpressionValidator(delegate?.visitGlobalVariable(globalType), localContext, listOf(globalType.contentType)), context)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
