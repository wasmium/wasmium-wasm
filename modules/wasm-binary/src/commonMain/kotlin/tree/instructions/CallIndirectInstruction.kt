package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class CallIndirectInstruction(
    public override val index: UInt,
    public override val reserved: Boolean,
) : AbstractInstruction(Opcode.CALL_INDIRECT), IndexInstruction, ReservedInstruction {
    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitCallIndirectInstruction(index, reserved)
    }
}
