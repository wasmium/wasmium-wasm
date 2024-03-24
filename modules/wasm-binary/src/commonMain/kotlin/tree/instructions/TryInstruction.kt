package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class TryInstruction(public override val blockType: Array<WasmType>) : AbstractInstruction(Opcode.TRY), TypeInstruction {
    public override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitTryInstruction(blockType)
    }
}
