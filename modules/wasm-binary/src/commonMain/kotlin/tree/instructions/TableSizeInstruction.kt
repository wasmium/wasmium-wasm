package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class TableSizeInstruction(public val tableIndex: UInt) : AbstractInstruction(Opcode.TABLE_SIZE) {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitTableSizeInstruction(tableIndex)
    }
}
