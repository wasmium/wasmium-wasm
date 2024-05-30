package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class CallIndirectInstruction(
    public override val index: UInt,
    public override val reserved: UInt,
) : AbstractInstruction(Opcode.CALL_INDIRECT), IndexInstruction, ReservedInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitCallIndirectInstruction(index, reserved)
    }
}
