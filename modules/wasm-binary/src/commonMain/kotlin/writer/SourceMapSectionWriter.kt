package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.visitors.SourceMapSectionVisitor

public class SourceMapSectionWriter(
    private val context: WriterContext,
    private val url: String,
) : SourceMapSectionVisitor {
    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeString(SectionName.SOURCE_MAPPING_URL.sectionName)
        payload.writeString(url)

        context.writer.writeSection(SectionKind.CUSTOM, buffer.toByteArray())
    }
}
