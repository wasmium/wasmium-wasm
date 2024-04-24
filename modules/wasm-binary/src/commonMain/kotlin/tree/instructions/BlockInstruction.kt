package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.BlockType
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class BlockInstruction(override val blockType: BlockType) : AbstractInstruction(Opcode.BLOCK), BlockTypeInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitBlockInstruction(blockType)
    }
}
