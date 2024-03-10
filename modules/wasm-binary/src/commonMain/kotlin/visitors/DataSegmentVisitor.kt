package org.wasmium.wasm.binary.visitors

public interface DataSegmentVisitor {
    public fun visitMemoryIndex(memoryIndex: UInt)

    public fun visitInitializerExpression(): InitializerExpressionVisitor

    public fun visitData(data: ByteArray)

    public fun visitEnd()
}
