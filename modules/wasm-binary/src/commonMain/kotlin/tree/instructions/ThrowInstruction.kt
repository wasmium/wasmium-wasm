package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class ThrowInstruction(override val index: UInt) : AbstractInstruction(Opcode.THROW), IndexInstruction {
    public override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitThrowInstruction(index)
    }
}
