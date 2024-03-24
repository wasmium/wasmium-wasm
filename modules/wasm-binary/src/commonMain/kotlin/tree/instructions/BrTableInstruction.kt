package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class BrTableInstruction(
    public override val targets: Array<UInt>,
    public override val defaultTarget: UInt
) : AbstractInstruction(Opcode.BR_TABLE), TableInstruction {
    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitBrTableInstruction(targets, defaultTarget)
    }
}
