package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.visitors.ExternalDebugSectionVisitor

public class ExternalDebugSectionWriter(
    private val context: WriterContext,
    private val url: String,
) : ExternalDebugSectionVisitor {
    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeString(SectionName.EXTERNAL_DEBUG_INFO.sectionName)
        payload.writeString(url)

        context.writer.writeSection(SectionKind.CUSTOM, buffer.toByteArray())
    }
}
