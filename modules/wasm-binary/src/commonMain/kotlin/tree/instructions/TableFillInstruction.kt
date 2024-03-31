package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class TableFillInstruction(public val tableIndex: UInt, public val target: UInt, public val value: UInt, public val size: UInt) : AbstractInstruction(Opcode.TABLE_FILL) {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitTableFillInstruction(tableIndex, target, value, size)
    }
}
