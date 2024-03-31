package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class ThrowRefInstruction : AbstractInstruction(Opcode.THROW_REF), NoneInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitThrowRefInstruction()
    }
}
