package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class TableCopyInstruction(
    public val targetTableIndex: UInt,
    public val sourceTableIndex: UInt,
) : AbstractInstruction(Opcode.TABLE_COPY) {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitTableCopyInstruction(targetTableIndex, sourceTableIndex)
    }
}
