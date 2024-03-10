package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class CurrentMemoryInstruction(override val reserved: Boolean) : NoneInstruction, ReservedInstruction {
    public override val opcode: Opcode = Opcode.MEMORY_SIZE

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitMemorySizeInstruction(reserved)
    }
}
