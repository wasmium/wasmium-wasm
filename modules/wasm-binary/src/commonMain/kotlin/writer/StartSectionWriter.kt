package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.StartSectionVisitor

public class StartSectionWriter(
    private val context: WriterContext,
    private val functionIndex: UInt,
) : StartSectionVisitor {
    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeIndex(functionIndex)

        context.writer.writeSection(SectionKind.START, context.options.isCanonical, buffer.toByteArray())
    }
}
