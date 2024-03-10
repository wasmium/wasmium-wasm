package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class CallIndirectInstruction(public override val index: UInt) : IndexInstruction, ReservedInstruction {
    override val opcode: Opcode = Opcode.CALL_INDIRECT
    override val reserved: Boolean = false

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitCallIndirectInstruction(index, reserved)
    }
}
