package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class BlockInstruction(override val blockType: Array<WasmType>) : TypeInstruction {
    public override val opcode: Opcode = Opcode.BLOCK

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitBlockInstruction(blockType)
    }
}
