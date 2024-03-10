package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class WrapInstruction(public override val opcode: Opcode) : NoneInstruction {
    public override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitWrapInstruction(opcode)
    }
}
