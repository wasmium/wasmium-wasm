package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public interface Instruction {
    /** The opcode of this instruction. */
    public val opcode: Opcode

    public fun accept(expressionVisitor: ExpressionVisitor)
}
