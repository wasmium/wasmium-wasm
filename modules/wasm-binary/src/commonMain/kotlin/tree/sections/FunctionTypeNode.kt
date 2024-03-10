package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.WasmType

public class FunctionTypeNode {
    public var typeIndex: UInt? = null
    public var parameters: Array<WasmType>? = null
    public var result: Array<WasmType>? = null
}
