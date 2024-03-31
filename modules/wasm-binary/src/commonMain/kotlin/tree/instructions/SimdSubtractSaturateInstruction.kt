package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class SimdSubtractSaturateInstruction(public override val opcode: Opcode) : NoneInstruction {
    public override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitSimdSubtractSaturateInstruction(opcode)
    }
}
