package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class BrIfInstruction(override val relativeDepth: UInt) : AbstractInstruction(Opcode.BR_IF), RelativeDepthInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitBrIfInstruction(relativeDepth)
    }
}
