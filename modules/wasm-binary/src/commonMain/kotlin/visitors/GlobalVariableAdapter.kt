package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public open class GlobalVariableAdapter(protected val delegate: GlobalVariableVisitor? = null) : GlobalVariableVisitor {
    public override fun visitInitializerExpression(): InitializerExpressionVisitor {
        if (delegate != null) {
            return InitializerExpressionAdapter(delegate.visitInitializerExpression())
        }

        return InitializerExpressionAdapter()
    }

    public override fun visitGlobalVariable(type: WasmType, mutable: Boolean) {
        delegate?.visitGlobalVariable(type, mutable)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
