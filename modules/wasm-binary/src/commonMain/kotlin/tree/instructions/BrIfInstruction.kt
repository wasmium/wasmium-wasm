package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class BrIfInstruction(override val relativeDepth: UInt) : AbstractInstruction(Opcode.BR_IF), RelativeDepthInstruction {
    override fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        functionBodyVisitor.visitBrIfInstruction(relativeDepth)
    }
}
