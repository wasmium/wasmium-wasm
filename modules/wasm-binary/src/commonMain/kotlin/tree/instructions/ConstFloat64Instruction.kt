package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class ConstFloat64Instruction(public override val value: Double) : AbstractInstruction(Opcode.F64_CONST), ConstantInstruction<Double> {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitConstFloat64Instruction(value)
    }
}
