package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.FunctionSectionVisitor

public class FunctionSectionNode : SectionNode(SectionKind.FUNCTION), FunctionSectionVisitor {
    public val types: MutableList<FunctionIndexTypeNode> = mutableListOf()

    public fun accept(functionSectionVisitor: FunctionSectionVisitor) {
        for (functionIndex in types) {
            functionSectionVisitor.visitFunction(functionIndex.functionIndex, functionIndex.typeIndex)
        }

        functionSectionVisitor.visitEnd()
    }

    public override fun visitFunction(functionIndex: UInt, typeIndex: UInt) {
        types.add(FunctionIndexTypeNode(functionIndex, typeIndex))
    }

    public override fun visitEnd() {
        // empty
    }
}
