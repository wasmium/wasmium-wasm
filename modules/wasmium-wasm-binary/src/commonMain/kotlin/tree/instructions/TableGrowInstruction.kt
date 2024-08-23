package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class TableGrowInstruction(public val tableIndex: UInt) : AbstractInstruction(Opcode.TABLE_GROW) {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitTableGrowInstruction(tableIndex)
    }
}
