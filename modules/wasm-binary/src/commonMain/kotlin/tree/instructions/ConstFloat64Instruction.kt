package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class ConstFloat64Instruction(public override val value: Double) : ConstantInstruction<Double> {
    override val opcode: Opcode = Opcode.F64_CONST

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitConstFloat64Instruction(value)
    }
}
