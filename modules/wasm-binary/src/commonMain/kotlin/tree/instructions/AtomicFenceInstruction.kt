package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class AtomicFenceInstruction(override val reserved: Boolean) : AbstractInstruction(Opcode.ATOMIC_FENCE), NoneInstruction, ReservedInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitAtomicFenceInstruction(reserved)
    }
}
