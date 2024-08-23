package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class ReferenceIsNullInstruction : AbstractInstruction(Opcode.REF_IS_NULL), NoneInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitReferenceIsNullInstruction()
    }
}
