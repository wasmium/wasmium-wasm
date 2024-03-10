package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class BrTableInstruction(
    public override val targets: Array<UInt>,
    public override val defaultTarget: UInt
) : TableInstruction {
    public override val opcode: Opcode = Opcode.BR_TABLE

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitBrTableInstruction(targets, defaultTarget)
    }
}
