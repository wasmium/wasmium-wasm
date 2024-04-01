package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.V128Value

public open class InitializerExpressionAdapter(protected val delegate: InitializerExpressionVisitor? = null) : InitializerExpressionVisitor {

    public override fun visitInitExprI32ConstExpr(value: Int): Unit = delegate?.visitInitExprI32ConstExpr(value) ?: Unit

    public override fun visitInitExprI64ConstExpr(value: Long): Unit = delegate?.visitInitExprI64ConstExpr(value) ?: Unit

    public override fun visitInitExprF32ConstExpr(value: Float): Unit = delegate?.visitInitExprF32ConstExpr(value) ?: Unit

    public override fun visitInitExprF64ConstExpr(value: Double): Unit = delegate?.visitInitExprF64ConstExpr(value) ?: Unit

    public override fun visitInitExprGetGlobalExpr(globalIndex: UInt): Unit = delegate?.visitInitExprGetGlobalExpr(globalIndex) ?: Unit

    public override fun visitInitExprV128ConstExpr(value: V128Value): Unit = delegate?.visitInitExprV128ConstExpr(value) ?: Unit

    public override fun visitInitExprEnd(): Unit = delegate?.visitInitExprEnd() ?: Unit

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
