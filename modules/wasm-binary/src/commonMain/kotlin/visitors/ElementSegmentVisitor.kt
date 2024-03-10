package org.wasmium.wasm.binary.visitors

public interface ElementSegmentVisitor {
    public fun visitTableIndex(tableIndex: UInt)

    public fun visitInitializerExpression(): InitializerExpressionVisitor

    public fun visitFunctionIndex(index: UInt, functionIndex: UInt)

    public fun visitEnd()
}
