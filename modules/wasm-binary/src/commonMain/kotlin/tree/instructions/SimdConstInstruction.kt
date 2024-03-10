package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class SimdConstInstruction(public override val value: V128Value) : ConstantInstruction<V128Value> {
    public override val opcode: Opcode = Opcode.V128_CONST

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitSimdConstInstruction(value)
    }
}
