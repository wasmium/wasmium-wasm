package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor

public class MemorySectionWriter(private val context: WriterContext) : MemorySectionVisitor {
    private var numberOfMemories = 0u
    private val body = ByteBuffer()

    public override fun visitMemory(limits: ResizableLimits) {
        WasmBinaryWriter(body).writeResizableLimits(limits)

        numberOfMemories++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfMemories)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.MEMORY, context.options.isCanonical, buffer.toByteArray())
    }
}
