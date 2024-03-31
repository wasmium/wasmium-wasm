package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.visitors.DataSegmentVisitor
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class DataSegmentWriter(
    private val context: WriterContext,
    private val body: ByteBuffer
) : DataSegmentVisitor {
    private val dataSegmentBuffer: ByteBuffer = ByteBuffer()

    public override fun visitActive(memoryIndex: UInt): InitializerExpressionVisitor {
        WasmBinaryWriter(dataSegmentBuffer).writeVarUInt32(memoryIndex)

        return InitializerExpressionWriter(context, dataSegmentBuffer)
    }

    public override fun visitData(data: ByteArray) {
        WasmBinaryWriter(dataSegmentBuffer).writeVarUInt32(data.size.toUInt())
        WasmBinaryWriter(dataSegmentBuffer).writeByteArray(data)
    }

    public override fun visitEnd() {
        WasmBinaryWriter(body).writeByteArray(dataSegmentBuffer.toByteArray())
    }
}