package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmSource
import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class MemorySectionReader(
    private val context: WasmBinaryContext,
) {
    public fun readMemorySection(source: WasmSource, visitor: ModuleVisitor) {
        context.numberMemories = source.readVarUInt32()
        if (context.numberMemories == 0u) {
            return
        }

        if (context.numberMemories > WasmBinary.MAX_MEMORIES) {
            throw ParserException("Number of memories ${context.numberMemories} exceed the maximum of ${WasmBinary.MAX_MEMORIES}")
        }

        val memoryVisitor: MemorySectionVisitor = visitor.visitMemorySection()
        for (index in 0u until context.numberMemories) {
            val memoryIndex = context.numberMemoryImports + index

            val limits: ResizableLimits = source.readResizableLimits()

            if (limits.initial > WasmBinary.MAX_MEMORY_PAGES) {
                throw ParserException("Invalid initial memory pages")
            }

            if (limits.isShared() && (limits.maximum == null)) {
                throw ParserException("Shared memory must have a max size")
            }

            if (limits.maximum != null) {
                if (limits.maximum > WasmBinary.MAX_MEMORY_PAGES) {
                    throw ParserException("Invalid memory max page")
                }

                if (limits.initial > limits.maximum) {
                    throw ParserException("Initial memory size greater than maximum")
                }
            }

            memoryVisitor.visitMemory(memoryIndex, limits)
        }

        memoryVisitor.visitEnd()
    }
}
