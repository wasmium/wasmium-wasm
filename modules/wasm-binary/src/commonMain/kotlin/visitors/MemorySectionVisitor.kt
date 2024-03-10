package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ResizableLimits

public interface MemorySectionVisitor {
    public fun visitMemory(memoryIndex: UInt, limits: ResizableLimits)

    public fun visitEnd()
}
