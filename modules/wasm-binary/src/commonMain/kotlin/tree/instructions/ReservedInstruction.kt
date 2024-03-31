package org.wasmium.wasm.binary.tree.instructions

public interface ReservedInstruction : Instruction {
    public val reserved: Boolean
}
