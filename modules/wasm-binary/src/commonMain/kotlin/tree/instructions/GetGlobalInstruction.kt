package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class GetGlobalInstruction(override val index: UInt) : IndexInstruction {
    public override val opcode: Opcode = Opcode.GET_GLOBAL

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitGetGlobalInstruction(index)
    }
}
