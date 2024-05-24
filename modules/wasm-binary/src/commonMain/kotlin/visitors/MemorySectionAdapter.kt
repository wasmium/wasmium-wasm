package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.MemoryLimits

public open class MemorySectionAdapter(protected val delegate: MemorySectionVisitor? = null) : MemorySectionVisitor {

    override fun visitMemory(limits: MemoryLimits): Unit = delegate?.visitMemory(limits) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
