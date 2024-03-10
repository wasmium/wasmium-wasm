package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class GetLocalInstruction(public override val index: UInt) : IndexInstruction {
    public override val opcode: Opcode = Opcode.GET_LOCAL

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitGetLocalInstruction(index)
    }
}
