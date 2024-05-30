package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class CurrentMemoryInstruction(override val reserved: UInt) : AbstractInstruction(Opcode.MEMORY_SIZE), NoneInstruction, ReservedInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitMemorySizeInstruction(reserved)
    }
}
