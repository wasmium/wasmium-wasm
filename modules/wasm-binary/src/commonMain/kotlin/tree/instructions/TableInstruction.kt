package org.wasmium.wasm.binary.tree.instructions

public interface TableInstruction : Instruction {
    public val targets: Array<UInt>
    public val defaultTarget: UInt
}
