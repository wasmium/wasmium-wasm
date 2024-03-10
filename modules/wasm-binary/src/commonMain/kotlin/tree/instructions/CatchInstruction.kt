package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class CatchInstruction : NoneInstruction {
    public override val opcode: Opcode = Opcode.CATCH

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitCatchInstruction()
    }
}
