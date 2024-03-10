package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.V128Value

public interface InitializerExpressionVisitor {
    public fun visitInitExprI32ConstExpr(value: Int)

    public fun visitInitExprI64ConstExpr(value: Long)

    public fun visitInitExprF32ConstExpr(value: Float)

    public fun visitInitExprF64ConstExpr(value: Double)

    public fun visitInitExprGetGlobalExpr(globalIndex: UInt)

    public fun visitInitExprV128ConstExpr(value: V128Value)

    public fun visitInitExprEnd()

    public fun visitEnd()
}
