package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class SetGlobalInstruction(override val index: UInt) : AbstractInstruction(Opcode.SET_GLOBAL), IndexInstruction {
    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitSetGlobalInstruction(index)
    }
}
