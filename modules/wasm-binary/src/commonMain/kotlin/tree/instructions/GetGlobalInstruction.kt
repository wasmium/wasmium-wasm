package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class GetGlobalInstruction(override val index: UInt) : AbstractInstruction(Opcode.GET_GLOBAL), IndexInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitGetGlobalInstruction(index)
    }
}
