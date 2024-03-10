package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class UnreachableInstruction : NoneInstruction {
    override val opcode: Opcode = Opcode.UNREACHABLE

    public override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitUnreachableInstruction()
    }
}
