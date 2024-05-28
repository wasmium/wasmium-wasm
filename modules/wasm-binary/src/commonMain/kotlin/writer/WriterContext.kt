package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.FunctionType

public class WriterContext(
    public val options: WriterOptions,
    public val writer: WasmBinaryWriter
) {
    public val functionTypes: MutableList<FunctionType> = mutableListOf()

}
