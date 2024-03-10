package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.V128Value

public open class InitializerExpressionAdapter(protected val delegate: InitializerExpressionVisitor? = null) : InitializerExpressionVisitor {
    public override fun visitInitExprI32ConstExpr(value: Int) {
        delegate?.visitInitExprI32ConstExpr(value)
    }

    public override fun visitInitExprI64ConstExpr(value: Long) {
        delegate?.visitInitExprI64ConstExpr(value)
    }

    public override fun visitInitExprF32ConstExpr(value: Float) {
        delegate?.visitInitExprF32ConstExpr(value)
    }

    public override fun visitInitExprF64ConstExpr(value: Double) {
        delegate?.visitInitExprF64ConstExpr(value)
    }

    public override fun visitInitExprGetGlobalExpr(globalIndex: UInt) {
        delegate?.visitInitExprGetGlobalExpr(globalIndex)
    }

    public override fun visitInitExprV128ConstExpr(value: V128Value) {
        delegate?.visitInitExprV128ConstExpr(value)
    }

    public override fun visitInitExprEnd() {
        delegate?.visitInitExprEnd()
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
