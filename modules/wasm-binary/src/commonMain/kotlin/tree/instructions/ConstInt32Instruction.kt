package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class ConstInt32Instruction(public override val value: Int) : AbstractInstruction(Opcode.I32_CONST), ConstantInstruction<Int> {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitConstInt32Instruction(value)
    }
}
