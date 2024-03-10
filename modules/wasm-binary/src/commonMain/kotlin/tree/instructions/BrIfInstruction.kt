package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class BrIfInstruction(override val relativeDepth: UInt) : RelativeDepthInstruction {
    public override val opcode: Opcode = Opcode.BR_IF

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitBrIfInstruction(relativeDepth)
    }
}
