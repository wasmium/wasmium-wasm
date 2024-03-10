package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class ConstFloat32Instruction(public override val value: Float) : ConstantInstruction<Float> {
    override val opcode: Opcode = Opcode.F32_CONST

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitConstFloat32Instruction(value)
    }
}
