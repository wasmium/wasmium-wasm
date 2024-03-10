package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class ThrowInstruction(override val index: UInt) : IndexInstruction {
    public override val opcode: Opcode = Opcode.THROW

    public override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitThrowInstruction(index)
    }
}
