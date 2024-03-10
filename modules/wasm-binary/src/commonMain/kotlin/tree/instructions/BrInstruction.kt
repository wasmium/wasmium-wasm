package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class BrInstruction(override val relativeDepth: UInt) : RelativeDepthInstruction {
    public override val opcode: Opcode = Opcode.BR

    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitBrInstruction(relativeDepth)
    }
}
