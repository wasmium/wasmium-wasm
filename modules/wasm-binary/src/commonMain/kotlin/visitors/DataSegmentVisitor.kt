package org.wasmium.wasm.binary.visitors

public interface DataSegmentVisitor {
    public fun visitMode(mode: UInt)

    public fun visitMemoryData(memoryIndex: UInt, data: ByteArray)

    public fun visitInitializerExpression(): InitializerExpressionVisitor

    public fun visitData(data: ByteArray)

    public fun visitEnd()
}
