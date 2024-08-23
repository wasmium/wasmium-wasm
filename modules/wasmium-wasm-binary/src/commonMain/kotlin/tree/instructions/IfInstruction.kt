package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.BlockType
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class IfInstruction(override val blockType: BlockType) : AbstractInstruction(Opcode.IF), BlockTypeInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitIfInstruction(blockType)
    }
}
