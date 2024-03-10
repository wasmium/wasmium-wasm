package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType

public interface TableSectionVisitor {
    public fun visitTable(tableIndex: UInt, elementType: WasmType, limits: ResizableLimits)

    public fun visitEnd()
}
