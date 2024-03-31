package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ResizableLimits

public open class MemorySectionAdapter(protected val delegate: MemorySectionVisitor? = null) : MemorySectionVisitor {
    public override fun visitMemory(limits: ResizableLimits) {
        delegate?.visitMemory(limits)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
