package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor

public class MemoryTypeNode(
    public val limits: ResizableLimits,
) {
    public fun accept(memorySectionVisitor: MemorySectionVisitor) {
        memorySectionVisitor.visitMemory(limits)
    }
}
