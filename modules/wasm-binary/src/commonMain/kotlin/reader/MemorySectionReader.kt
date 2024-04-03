package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class MemorySectionReader(
    private val context: ReaderContext,
) {
    public fun readMemorySection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfMemories = source.readVarUInt32()
        if (context.numberOfMemories == 0u) {
            return
        }

        val memoryVisitor = visitor.visitMemorySection()
        for (index in 0u until context.numberOfMemories) {
            val memoryIndex = context.numberOfMemoryImports + index

            val limits = source.readResizableLimits()

            if (limits.isShared() && (limits.maximum == null)) {
                throw ParserException("Shared memory must have a max size")
            }

            if (limits.maximum != null) {
                if (limits.initial > limits.maximum) {
                    throw ParserException("Initial memory size greater than maximum")
                }
            }

            memoryVisitor.visitMemory(limits)
        }

        memoryVisitor.visitEnd()
    }
}
