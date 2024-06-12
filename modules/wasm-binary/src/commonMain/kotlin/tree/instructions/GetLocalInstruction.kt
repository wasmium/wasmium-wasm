package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class GetLocalInstruction(public override val index: UInt) : AbstractInstruction(Opcode.GET_LOCAL), IndexInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitGetLocalInstruction(index)
    }
}
