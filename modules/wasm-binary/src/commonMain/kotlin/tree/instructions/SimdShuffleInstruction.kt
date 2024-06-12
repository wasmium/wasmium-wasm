package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class SimdShuffleInstruction(
    public override val opcode: Opcode,
    public override val value: V128Value
) : ConstantInstruction<V128Value> {
    public override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitSimdShuffleInstruction(opcode, value)
    }
}
