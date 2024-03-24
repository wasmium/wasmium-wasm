package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class ConstInt64Instruction(public override val value: Long) : AbstractInstruction(Opcode.I64_CONST), ConstantInstruction<Long> {
    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitConstInt64Instruction(value)
    }
}
