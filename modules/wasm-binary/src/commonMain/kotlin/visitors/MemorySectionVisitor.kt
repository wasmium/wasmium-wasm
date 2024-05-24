package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.MemoryLimits

public interface MemorySectionVisitor {

    public fun visitMemory(limits: MemoryLimits)

    public fun visitEnd()
}
