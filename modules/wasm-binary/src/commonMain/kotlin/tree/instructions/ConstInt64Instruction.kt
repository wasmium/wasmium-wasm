package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class ConstInt64Instruction(public override val value: Long) : ConstantInstruction<Long> {
    override val opcode: Opcode = Opcode.I64_CONST

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitConstInt64Instruction(value)
    }
}
