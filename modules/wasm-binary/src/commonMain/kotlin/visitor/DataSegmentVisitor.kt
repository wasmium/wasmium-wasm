package org.wasmium.wasm.binary.visitors

public interface DataSegmentVisitor {

    public fun visitActive(memoryIndex: UInt): ExpressionVisitor

    public fun visitData(data: ByteArray)

    public fun visitEnd()

}