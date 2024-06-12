package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class SetLocalInstruction(override val index: UInt) : AbstractInstruction(Opcode.SET_LOCAL), IndexInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitSetLocalInstruction(index)
    }
}
