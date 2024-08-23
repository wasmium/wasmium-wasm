package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.FunctionType
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitor.FunctionSectionVisitor

public class FunctionSectionNode : SectionNode(SectionKind.FUNCTION), FunctionSectionVisitor {
    public val functionTypes: MutableList<FunctionType> = mutableListOf()

    public fun accept(functionSectionVisitor: FunctionSectionVisitor) {
        for (functionType in functionTypes) {
            functionSectionVisitor.visitFunction(functionType)
        }

        functionSectionVisitor.visitEnd()
    }

    public override fun visitFunction(functionType: FunctionType) {
        functionTypes.add(functionType)
    }

    public override fun visitEnd() {
        // empty
    }
}
