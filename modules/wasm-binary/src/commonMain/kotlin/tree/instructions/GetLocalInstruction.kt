package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class GetLocalInstruction(public override val index: UInt) : AbstractInstruction(Opcode.GET_LOCAL), IndexInstruction {
    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitGetLocalInstruction(index)
    }
}
