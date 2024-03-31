package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class EndInstruction : AbstractInstruction(Opcode.END), NoneInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitEndInstruction()
    }
}
