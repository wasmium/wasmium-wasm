package org.wasmium.wasm.binary

public class ParserException(
    override val message: String,
) : WasmBinaryException()
