package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class SelectTypedInstruction(private val types: List<WasmType>) : AbstractInstruction(Opcode.SELECT_T) {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitSelectTypedInstruction(types)
    }
}
