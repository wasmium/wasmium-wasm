package org.wasmium.wasm.binary.tree.instructions

public interface AlignOffsetInstruction : Instruction {
    public val alignment: UInt
    public val offset: UInt
}
