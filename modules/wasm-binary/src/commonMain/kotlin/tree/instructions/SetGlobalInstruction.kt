package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class SetGlobalInstruction(override val index: UInt) : AbstractInstruction(Opcode.SET_GLOBAL), IndexInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitSetGlobalInstruction(index)
    }
}
