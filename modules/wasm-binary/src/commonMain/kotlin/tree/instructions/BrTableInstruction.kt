package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class BrTableInstruction(
    public override val targets: List<UInt>,
    public override val defaultTarget: UInt
) : AbstractInstruction(Opcode.BR_TABLE), TableInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitBrTableInstruction(targets, defaultTarget)
    }
}
