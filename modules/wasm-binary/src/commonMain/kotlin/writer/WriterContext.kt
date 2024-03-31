package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.WasmBinaryWriter

public class WriterContext(
    public val options: WriterOptions,
    public val writer: WasmBinaryWriter
)
