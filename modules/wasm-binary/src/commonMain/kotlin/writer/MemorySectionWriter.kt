package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.MemoryType
import org.wasmium.wasm.binary.visitor.MemorySectionVisitor

public class MemorySectionWriter(private val context: WriterContext) : MemorySectionVisitor {
    private var numberOfMemories = 0u
    private val body = ByteBuffer()
    private val writer = WasmBinaryWriter(body)

    public override fun visitMemory(memoryType: MemoryType) {
        writer.writeMemoryType(memoryType)

        numberOfMemories++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfMemories)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.MEMORY, buffer.toByteArray())
    }
}
