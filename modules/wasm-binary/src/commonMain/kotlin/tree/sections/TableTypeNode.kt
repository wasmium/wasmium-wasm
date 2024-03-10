package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType

public class TableTypeNode {
    public var tableIndex: UInt? = null
    public var elementType: WasmType? = null
    public var limits: ResizableLimits? = null
}
