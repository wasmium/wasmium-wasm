package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.MemoryType

public open class MemorySectionAdapter(protected val delegate: MemorySectionVisitor? = null) : MemorySectionVisitor {

    override fun visitMemory(memoryType: MemoryType): Unit = delegate?.visitMemory(memoryType) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
