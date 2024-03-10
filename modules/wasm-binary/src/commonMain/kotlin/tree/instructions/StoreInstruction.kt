package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class StoreInstruction(
    public override val opcode: Opcode,
    public override val alignment: UInt,
    public override val offset: UInt,
) : AlignOffsetInstruction {
    public override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitStoreInstruction(opcode, alignment, offset)
    }
}
