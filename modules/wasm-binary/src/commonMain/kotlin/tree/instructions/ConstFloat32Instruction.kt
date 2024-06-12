package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class ConstFloat32Instruction(public override val value: Float) : AbstractInstruction(Opcode.F32_CONST), ConstantInstruction<Float> {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitConstFloat32Instruction(value)
    }
}
