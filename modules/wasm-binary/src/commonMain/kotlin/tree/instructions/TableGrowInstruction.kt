package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class TableGrowInstruction(public val tableIndex: UInt, public val value: UInt, public val delta: UInt) : AbstractInstruction(Opcode.TABLE_GROW) {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitTableGrowInstruction(tableIndex, value, delta)
    }
}
