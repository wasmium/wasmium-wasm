package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class MemoryInitInstruction(
    public val memoryIndex: UInt,
    public val segmentIndex: UInt,
) : AbstractInstruction(Opcode.MEMORY_INIT) {
    public override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitMemoryInitInstruction(memoryIndex, segmentIndex)
    }
}
