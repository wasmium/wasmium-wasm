package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.MemoryType
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor

public class MemorySectionNode : SectionNode(SectionKind.MEMORY), MemorySectionVisitor {
    public val memoryTypes: MutableList<MemoryType> = mutableListOf()

    public fun accept(memorySectionVisitor: MemorySectionVisitor) {
        for (memoryType in memoryTypes) {
            memorySectionVisitor.visitMemory(memoryType)
        }

        memorySectionVisitor.visitEnd()
    }

    public override fun visitMemory(memoryType: MemoryType) {
        memoryTypes.add(memoryType)
    }

    public override fun visitEnd() {
        // empty
    }
}
