package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class SimdConstInstruction(public override val value: V128Value) : AbstractInstruction(Opcode.V128_CONST), ConstantInstruction<V128Value> {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitSimdConstInstruction(value)
    }
}
