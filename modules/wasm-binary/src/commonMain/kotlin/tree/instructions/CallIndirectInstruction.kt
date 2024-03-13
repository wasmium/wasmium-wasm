package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class CallIndirectInstruction(
    public override val index: UInt,
    public override val reserved: Boolean,
) : IndexInstruction, ReservedInstruction {
    override val opcode: Opcode = Opcode.CALL_INDIRECT


    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitCallIndirectInstruction(index, reserved)
    }
}
