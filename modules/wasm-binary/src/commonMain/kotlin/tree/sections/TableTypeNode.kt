package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType

public class TableTypeNode(
    public val tableIndex: UInt,
    public val elementType: WasmType,
    public val limits: ResizableLimits,
)
