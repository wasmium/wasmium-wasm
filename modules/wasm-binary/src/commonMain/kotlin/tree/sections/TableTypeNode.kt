package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.ResizableLimits

public class TableTypeNode {
    public var tableIndex: UInt? = null
    public var elementType: WasmType? = null
    public var limits: ResizableLimits? = null
}
