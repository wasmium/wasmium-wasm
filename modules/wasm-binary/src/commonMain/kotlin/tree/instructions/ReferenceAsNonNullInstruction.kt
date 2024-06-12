package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class ReferenceAsNonNullInstruction : AbstractInstruction(Opcode.REF_AS_NON_NULL), NoneInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitReferenceAsNonNullInstruction()
    }
}
