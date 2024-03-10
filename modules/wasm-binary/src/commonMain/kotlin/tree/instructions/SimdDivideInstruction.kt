package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class SimdDivideInstruction(public override val opcode: Opcode) : NoneInstruction {
    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitSimdDivideInstruction(opcode)
    }
}