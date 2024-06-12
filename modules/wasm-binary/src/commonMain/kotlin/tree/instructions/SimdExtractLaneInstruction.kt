package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class SimdExtractLaneInstruction(override val opcode: Opcode, public val index: UInt) : NoneInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitSimdExtractLaneInstruction(opcode, index)
    }
}
