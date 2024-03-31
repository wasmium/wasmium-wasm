package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class ConstInt64Instruction(public override val value: Long) : AbstractInstruction(Opcode.I64_CONST), ConstantInstruction<Long> {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitConstInt64Instruction(value)
    }
}
