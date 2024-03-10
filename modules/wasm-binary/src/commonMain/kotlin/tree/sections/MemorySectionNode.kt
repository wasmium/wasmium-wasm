package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor

public class MemorySectionNode : SectionNode(SectionKind.MEMORY), MemorySectionVisitor {
    public val memories: MutableList<MemoryTypeNode> = mutableListOf()

    public fun accept(memorySectionVisitor: MemorySectionVisitor) {
        for (memoryType in memories) {
            memorySectionVisitor.visitMemory(memoryType.memoryIndex!!, memoryType.limits!!)
        }
    }

    public override fun visitMemory(memoryIndex: UInt, limits: ResizableLimits) {
        val memoryType = MemoryTypeNode()
        memoryType.memoryIndex = memoryIndex
        memoryType.limits = limits

        memories.add(memoryType)
    }

    public override fun visitEnd() {
        // empty
    }

}