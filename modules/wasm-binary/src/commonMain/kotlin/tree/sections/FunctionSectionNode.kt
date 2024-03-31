package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.FunctionSectionVisitor

public class FunctionSectionNode : SectionNode(SectionKind.FUNCTION), FunctionSectionVisitor {
    public val types: MutableList<UInt> = mutableListOf()

    public fun accept(functionSectionVisitor: FunctionSectionVisitor) {
        for (typeIndex in types) {
            functionSectionVisitor.visitFunction(typeIndex)
        }

        functionSectionVisitor.visitEnd()
    }

    public override fun visitFunction(typeIndex: UInt) {
        types.add(typeIndex)
    }

    public override fun visitEnd() {
        // empty
    }
}
