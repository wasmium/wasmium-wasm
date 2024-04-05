package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.WasmType

public interface TypeInstruction : Instruction {
    public val blockType: List<WasmType>
}
