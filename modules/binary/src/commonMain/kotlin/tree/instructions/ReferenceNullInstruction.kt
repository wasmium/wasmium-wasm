package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class ReferenceNullInstruction(public val type: WasmType) : AbstractInstruction(Opcode.REF_NULL), NoneInstruction {
    override fun accept(expressionVisitor: ExpressionVisitor) {
        expressionVisitor.visitReferenceNullInstruction(type)
    }
}
