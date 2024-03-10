package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public interface Instruction {
    public val opcode: Opcode

    public fun accept(functionBodyVisitor: FunctionBodyVisitor)
}

