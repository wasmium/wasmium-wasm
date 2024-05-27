package org.wasmium.wasm.binary.tree

public class FunctionType(
    public var parameters: List<WasmType>,
    public var results: List<WasmType>,
)
