package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class SimdSplatInstruction(
    public override val opcode: Opcode,
    public override val value: UInt
) : ConstantInstruction<UInt> {
    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitSimdSplatInstruction(opcode, value)
    }
}
