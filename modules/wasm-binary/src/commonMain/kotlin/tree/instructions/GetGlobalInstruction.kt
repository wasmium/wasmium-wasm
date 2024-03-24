package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class GetGlobalInstruction(override val index: UInt) : AbstractInstruction(Opcode.GET_GLOBAL), IndexInstruction {
    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitGetGlobalInstruction(index)
    }
}
