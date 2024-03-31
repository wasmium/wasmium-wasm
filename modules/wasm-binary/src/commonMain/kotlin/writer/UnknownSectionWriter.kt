package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.UnknownSectionVisitor

public class UnknownSectionWriter(
    private val context: WriterContext,
    private val name: String,
    private val content: ByteArray
) : UnknownSectionVisitor {
    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeString(name)
        payload.writeByteArray(content)

        context.writer.writeSection(SectionKind.CUSTOM, context.options.isCanonical, buffer.toByteArray())
    }
}
