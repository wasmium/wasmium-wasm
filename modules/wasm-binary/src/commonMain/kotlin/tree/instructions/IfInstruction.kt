package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class IfInstruction(public override val blockType: Array<WasmType>) : AbstractInstruction(Opcode.IF), TypeInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitIfInstruction(blockType)
    }
}
