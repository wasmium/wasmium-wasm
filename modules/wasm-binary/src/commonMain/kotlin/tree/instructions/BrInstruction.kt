package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class BrInstruction(override val relativeDepth: UInt) : AbstractInstruction(Opcode.BR), RelativeDepthInstruction {
    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitBrInstruction(relativeDepth)
    }
}
