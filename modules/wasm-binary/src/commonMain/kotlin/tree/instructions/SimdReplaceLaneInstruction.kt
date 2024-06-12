package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class SimdReplaceLaneInstruction(
    public override val opcode: Opcode,
    public override val index: UInt
) : IndexInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitSimdReplaceLaneInstruction(opcode, index)
    }
}
