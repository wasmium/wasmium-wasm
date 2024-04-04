package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class ReferenceFunctionInstruction(override val index: UInt) : AbstractInstruction(Opcode.REF_FUNC), IndexInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitReferenceFunctionInstruction(index)
    }
}
