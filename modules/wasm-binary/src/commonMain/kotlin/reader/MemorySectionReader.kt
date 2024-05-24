package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class MemorySectionReader(private val context: ReaderContext) {
    public fun readMemorySection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfMemories = source.readVarUInt32()

        if (context.numberOfMemories > 0u) {
            val memoryVisitor = visitor.visitMemorySection()
            for (index in 0u until context.numberOfMemories) {
                val memoryType = source.readMemoryType()

                memoryVisitor.visitMemory(memoryType)
            }

            memoryVisitor.visitEnd()
        }
    }
}
