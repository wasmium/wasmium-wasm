package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.visitors.DataSegmentVisitor
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class DataSegmentWriter(
    private val context: WriterContext,
    private val body: ByteBuffer
) : DataSegmentVisitor {
    private val buffer: ByteBuffer = ByteBuffer()
    private val writer = WasmBinaryWriter(buffer)
    private var active: Boolean = false

    public override fun visitActive(memoryIndex: UInt): ExpressionVisitor {
        active = true

        if(memoryIndex == 0u) {
            writer.writeVarUInt32(WasmBinary.DATA_ACTIVE.toUInt())
        } else {
            writer.writeVarUInt32(WasmBinary.DATA_EXPLICIT.toUInt())
            writer.writeVarUInt32(memoryIndex)
        }

        return ExpressionWriter(context, buffer)
    }

    public override fun visitData(data: ByteArray) {
        writer.writeVarUInt32(data.size.toUInt())
        writer.writeByteArray(data)
    }

    public override fun visitEnd() {
        if (!active) {
            WasmBinaryWriter(body).writeVarUInt32(WasmBinary.DATA_PASSIVE.toUInt())
        }

        WasmBinaryWriter(body).writeByteArray(buffer.toByteArray())
    }
}
