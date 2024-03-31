package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.DataCountSectionAdapter

public class DataCountSectionWriter(
    private val context: WriterContext,
    private val dataCount: UInt,
) : DataCountSectionAdapter() {
    public override fun visitEnd() {
        val sectionBuffer = ByteBuffer()
        val payload = WasmBinaryWriter(sectionBuffer)

        payload.writeVarUInt32(dataCount)

        context.writer.writeSection(SectionKind.DATA_COUNT, context.options.isCanonical, sectionBuffer.toByteArray())
    }
}
