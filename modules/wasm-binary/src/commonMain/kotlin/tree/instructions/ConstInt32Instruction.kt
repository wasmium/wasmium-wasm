package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class ConstInt32Instruction(public override val value: Int) : ConstantInstruction<Int> {
    override val opcode: Opcode = Opcode.I32_CONST

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitConstInt32Instruction(value)
    }
}
