package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.DataSectionVisitor
import org.wasmium.wasm.binary.visitors.DataSegmentVisitor

public class DataSectionWriter(private val context: WriterContext) : DataSectionVisitor {
    private var numberOfSegments = 0u
    private val body = ByteBuffer()

    public override fun visitDataSegment(): DataSegmentVisitor {
        numberOfSegments++

        return DataSegmentWriter(context, body)
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfSegments)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.DATA, context.options.isCanonical, buffer.toByteArray())
    }
}
