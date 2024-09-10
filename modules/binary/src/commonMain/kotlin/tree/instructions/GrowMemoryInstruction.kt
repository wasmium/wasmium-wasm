package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class GrowMemoryInstruction(override val reserved: UInt) : AbstractInstruction(Opcode.MEMORY_GROW), NoneInstruction, ReservedInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitMemoryGrowInstruction(reserved)
    }
}
