package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class TableSetTableInstruction(public val tableIndex: UInt) : AbstractInstruction(Opcode.SET_TABLE) {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitSetTableInstruction(tableIndex)
    }
}
