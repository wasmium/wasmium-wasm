package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class TeeLocalInstruction(override val index: UInt) : AbstractInstruction(Opcode.TEE_LOCAL), IndexInstruction {
    public override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitTeeLocalInstruction(index)
    }
}
