package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class ConstFloat64Instruction(public override val value: Double) : AbstractInstruction(Opcode.F64_CONST), ConstantInstruction<Double> {
    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitConstFloat64Instruction(value)
    }
}
