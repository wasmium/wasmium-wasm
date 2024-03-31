package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class UnreachableInstruction : AbstractInstruction(Opcode.UNREACHABLE), NoneInstruction {
    public override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitUnreachableInstruction()
    }
}
