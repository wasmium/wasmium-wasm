package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.BlockType
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class TryTableInstruction(override val blockType: BlockType, public val handlers: List<TryCatchArgument>) : AbstractInstruction(Opcode.TRY_TABLE), BlockTypeInstruction {
    public override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitTryTableInstruction(blockType, handlers)
    }
}
