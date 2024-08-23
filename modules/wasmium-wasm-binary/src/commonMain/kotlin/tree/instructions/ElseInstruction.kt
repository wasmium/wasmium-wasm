package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class ElseInstruction : AbstractInstruction(Opcode.ELSE), NoneInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitElseInstruction()
    }
}
