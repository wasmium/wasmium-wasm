package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class ThrowRefInstruction : NoneInstruction {
    public override val opcode: Opcode = Opcode.THROW_REF

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitThrowRefInstruction()
    }
}
