package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ResizableLimits

public class MemorySectionAdapter(protected val delegate: MemorySectionVisitor? = null) : MemorySectionVisitor {
    public override fun visitMemory(memoryIndex: UInt, limits: ResizableLimits) {
        delegate?.visitMemory(memoryIndex, limits)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
