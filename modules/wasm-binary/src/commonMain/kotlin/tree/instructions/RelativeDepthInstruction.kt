package org.wasmium.wasm.binary.tree.instructions

public interface RelativeDepthInstruction : Instruction {
    public val relativeDepth: UInt
}
