package org.wasmium.wasm.binary.tree.instructions

public interface ConstantInstruction<out T>: Instruction {
    public val value: T
}
