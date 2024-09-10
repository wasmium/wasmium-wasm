package org.wasmium.wasm.binary.tree

public data class FunctionType(
    public var form: WasmType,
    public var parameters: List<WasmType>,
    public var results: List<WasmType>,
)
