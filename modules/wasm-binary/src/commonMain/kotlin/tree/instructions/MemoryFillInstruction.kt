package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class MemoryFillInstruction(
    public val memoryIndex: UInt,
) : AbstractInstruction(Opcode.MEMORY_FILL) {
    public override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitMemoryFillInstruction(memoryIndex)
    }
}
