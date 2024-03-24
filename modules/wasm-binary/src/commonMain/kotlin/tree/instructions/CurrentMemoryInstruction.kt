package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class CurrentMemoryInstruction(override val reserved: Boolean) : AbstractInstruction(Opcode.MEMORY_SIZE), NoneInstruction, ReservedInstruction {
    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitMemorySizeInstruction(reserved)
    }
}
