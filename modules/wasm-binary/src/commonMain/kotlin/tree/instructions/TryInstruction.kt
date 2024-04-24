package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.BlockType
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class TryInstruction(override val blockType: BlockType) : AbstractInstruction(Opcode.TRY), BlockTypeInstruction {
    public override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitTryInstruction(blockType)
    }
}
