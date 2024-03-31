package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class GrowMemoryInstruction(override val reserved: Boolean) : AbstractInstruction(Opcode.MEMORY_GROW), NoneInstruction, ReservedInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitMemoryGrowInstruction(reserved)
    }
}
