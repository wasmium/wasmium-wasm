package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class CallInstruction(override val index: UInt) : IndexInstruction {
    public override val opcode: Opcode = Opcode.CALL

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitCallInstruction(index)
    }
}
