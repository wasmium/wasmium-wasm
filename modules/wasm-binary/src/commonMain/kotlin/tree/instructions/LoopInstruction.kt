package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class LoopInstruction(public override val blockType: List<WasmType>) : AbstractInstruction(Opcode.LOOP), TypeInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitLoopInstruction(blockType)
    }
}
