package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class TableFillInstruction(
    public val tableIndex: UInt,
) : AbstractInstruction(Opcode.TABLE_FILL) {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitTableFillInstruction(tableIndex)
    }
}
