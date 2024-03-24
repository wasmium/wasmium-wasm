package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class SetLocalInstruction(override val index: UInt) : AbstractInstruction(Opcode.SET_LOCAL), IndexInstruction {
    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitSetLocalInstruction(index)
    }
}
