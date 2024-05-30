package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.MemoryType

public interface MemorySectionVisitor {

    public fun visitMemory(memoryType: MemoryType)

    public fun visitEnd()
}
