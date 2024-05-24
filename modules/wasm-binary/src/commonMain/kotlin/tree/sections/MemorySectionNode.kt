package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.MemoryLimits
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor

public class MemorySectionNode : SectionNode(SectionKind.MEMORY), MemorySectionVisitor {
    public val memories: MutableList<MemoryType> = mutableListOf()

    public fun accept(memorySectionVisitor: MemorySectionVisitor) {
        for (memoryType in memories) {
            memorySectionVisitor.visitMemory(memoryType.limits)
        }

        memorySectionVisitor.visitEnd()
    }

    public override fun visitMemory(limits: MemoryLimits) {
        memories.add(MemoryType(limits))
    }

    public override fun visitEnd() {
        // empty
    }
}
