package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class DataDropInstruction(public val segmentIndex: UInt) : AbstractInstruction(Opcode.DATA_DROP) {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitDataDropInstruction(segmentIndex)
    }
}
