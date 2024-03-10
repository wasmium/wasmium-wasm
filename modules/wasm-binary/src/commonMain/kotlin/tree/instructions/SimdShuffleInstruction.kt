package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class SimdShuffleInstruction(override val opcode: Opcode, override val value: V128Value) : ConstantInstruction<V128Value> {
    public override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitSimdShuffleInstruction(opcode, value)
    }
}
