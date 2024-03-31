package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class AtomicRmwCompareExchangeInstruction(
    public override val opcode: Opcode,
    public override val alignment: UInt,
    public override val offset: UInt
) : AlignOffsetInstruction {
    public override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitAtomicRmwCompareExchangeInstruction(opcode, alignment, offset)
    }
}
