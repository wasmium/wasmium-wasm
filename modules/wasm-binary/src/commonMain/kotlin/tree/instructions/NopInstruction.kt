package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class NopInstruction : NoneInstruction {
    override val opcode: Opcode = Opcode.NOP

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitNopInstruction()
    }
}
