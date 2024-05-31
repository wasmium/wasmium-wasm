package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class TableGetTableInstruction(public val tableIndex: UInt) : AbstractInstruction(Opcode.GET_TABLE) {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitGetTableInstruction(tableIndex)
    }
}
