package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class ElementDropInstruction(public val segmentIndex: UInt) : AbstractInstruction(Opcode.ELEMENT_DROP) {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitElementDropInstruction(segmentIndex)
    }
}
