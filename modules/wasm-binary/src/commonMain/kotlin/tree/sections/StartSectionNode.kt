package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.StartSectionVisitor

public class StartSectionNode : SectionNode(SectionKind.START), StartSectionVisitor {
    public var functionIndex: UInt? = null

    public fun accept(startSectionVisitor: StartSectionVisitor) {
        startSectionVisitor.visitStartFunctionIndex(functionIndex!!)
    }

    override fun visitStartFunctionIndex(functionIndex: UInt) {
        this.functionIndex = functionIndex
    }

    public override fun visitEnd() {
        // empty
    }
}
