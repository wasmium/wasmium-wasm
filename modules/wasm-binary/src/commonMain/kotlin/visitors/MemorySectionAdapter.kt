package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ResizableLimits

public open class MemorySectionAdapter(protected val delegate: MemorySectionVisitor? = null) : MemorySectionVisitor {

    public override fun visitMemory(limits: ResizableLimits): Unit = delegate?.visitMemory(limits) ?: Unit

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
