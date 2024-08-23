package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.BlockType

public interface BlockTypeInstruction : Instruction {
    public val blockType: BlockType
}
