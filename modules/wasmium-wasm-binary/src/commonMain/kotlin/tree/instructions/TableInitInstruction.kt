package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class TableInitInstruction(
    public val segmentIndex: UInt,
    public val tableIndex: UInt,
) : AbstractInstruction(Opcode.TABLE_INIT) {
    public override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitTableInitInstruction(segmentIndex, tableIndex)
    }
}
