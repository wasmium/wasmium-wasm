package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.FunctionType
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.TypeSectionVisitor

public class TypeSectionNode : SectionNode(SectionKind.TYPE), TypeSectionVisitor {
    public val types: MutableList<FunctionType> = mutableListOf()

    public fun accept(typeSectionVisitor: TypeSectionVisitor) {
        for (functionType in types) {
            typeSectionVisitor.visitType(functionType)
        }

        typeSectionVisitor.visitEnd()
    }

    public override fun visitType(functionType: FunctionType) {
        types.add(functionType)
    }

    public override fun visitEnd() {
        // empty
    }
}
