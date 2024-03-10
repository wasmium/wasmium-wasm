package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class TeeLocalInstruction(override val index: UInt) : IndexInstruction {
    public override val opcode: Opcode = Opcode.TEE_LOCAL

    public override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitTeeLocalInstruction(index)
    }
}
