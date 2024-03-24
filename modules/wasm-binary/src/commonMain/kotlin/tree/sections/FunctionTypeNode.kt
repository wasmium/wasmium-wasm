package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.WasmType

public class FunctionTypeNode(
    public var typeIndex: UInt,
    public var parameters: Array<WasmType>,
    public var results: Array<WasmType>,
)
