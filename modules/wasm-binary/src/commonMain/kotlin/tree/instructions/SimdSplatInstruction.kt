package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class SimdSplatInstruction(
    public override val opcode: Opcode,
    public override val value: UInt
) : ConstantInstruction<UInt> {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitSimdSplatInstruction(opcode, value)
    }
}
