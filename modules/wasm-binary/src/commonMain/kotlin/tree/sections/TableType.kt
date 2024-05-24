package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.MemoryLimits
import org.wasmium.wasm.binary.tree.WasmType

public class TableType(
    public val elementType: WasmType,
    public val limits: MemoryLimits,
)
