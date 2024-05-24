package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.MemoryLimits
import org.wasmium.wasm.binary.tree.WasmType

public interface TableSectionVisitor {

    public fun visitTable(elementType: WasmType, limits: MemoryLimits)

    public fun visitEnd()
}
