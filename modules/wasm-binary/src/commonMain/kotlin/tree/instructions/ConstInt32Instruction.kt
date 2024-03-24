package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class ConstInt32Instruction(public override val value: Int) : AbstractInstruction(Opcode.I32_CONST), ConstantInstruction<Int> {
    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitConstInt32Instruction(value)
    }
}
