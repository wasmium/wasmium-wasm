package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class SetLocalInstruction(override val index: UInt) : IndexInstruction {
    public override val opcode: Opcode = Opcode.SET_LOCAL

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitSetLocalInstruction(index)
    }
}
