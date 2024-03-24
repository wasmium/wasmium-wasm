package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class TeeLocalInstruction(override val index: UInt) : AbstractInstruction(Opcode.TEE_LOCAL), IndexInstruction {
    public override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitTeeLocalInstruction(index)
    }
}
