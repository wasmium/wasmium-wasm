package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class AtomicWaitInstruction(
    public override val opcode: Opcode,
    public override val alignment: UInt,
    public override val offset: UInt
) : AlignOffsetInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitAtomicWaitInstruction(opcode, alignment, offset)
    }
}
