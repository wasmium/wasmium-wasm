package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class LessEqualInstruction(public override val opcode: Opcode) : NoneInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitLessEqualInstruction(opcode)
    }
}
