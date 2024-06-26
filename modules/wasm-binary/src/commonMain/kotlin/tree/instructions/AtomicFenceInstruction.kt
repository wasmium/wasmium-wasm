package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class AtomicFenceInstruction(override val reserved: UInt) : AbstractInstruction(Opcode.ATOMIC_FENCE), NoneInstruction, ReservedInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitAtomicFenceInstruction(reserved)
    }
}
