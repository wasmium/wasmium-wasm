package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class TryInstruction(public override val blockType: Array<WasmType>) : TypeInstruction {
    public override val opcode: Opcode = Opcode.TRY

    public override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitTryInstruction(blockType)
    }
}
