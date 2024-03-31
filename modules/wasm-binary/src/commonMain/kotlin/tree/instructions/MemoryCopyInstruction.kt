package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class MemoryCopyInstruction(
    public val targetIndex: UInt,
    public val sourceIndex: UInt,
    public val targetOffset: UInt,
    public val sourceOffset: UInt,
    public val size: UInt,
) : AbstractInstruction(Opcode.MEMORY_COPY) {
    public override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitMemoryCopyInstruction(targetIndex, sourceIndex, targetOffset, sourceOffset, size)
    }
}
