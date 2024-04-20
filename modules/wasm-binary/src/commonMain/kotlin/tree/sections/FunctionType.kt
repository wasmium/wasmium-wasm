package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.WasmType

public class FunctionType(
    public var parameters: List<WasmType>,
    public var results: List<WasmType>,
)
