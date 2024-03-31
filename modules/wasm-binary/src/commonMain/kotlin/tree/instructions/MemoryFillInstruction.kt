package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class MemoryFillInstruction(
    public val address: UInt,
    public val offset: UInt,
    public val size: UInt,
) : AbstractInstruction(Opcode.MEMORY_FILL) {
    public override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitMemoryFillInstruction(address, offset, size, size)
    }
}
