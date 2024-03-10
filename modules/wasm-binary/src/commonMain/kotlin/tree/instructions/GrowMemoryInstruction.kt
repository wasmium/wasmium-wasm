package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class GrowMemoryInstruction(override val reserved: Boolean) : NoneInstruction, ReservedInstruction {
    public override val opcode: Opcode = Opcode.MEMORY_GROW

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitMemoryGrowInstruction(reserved)
    }
}
