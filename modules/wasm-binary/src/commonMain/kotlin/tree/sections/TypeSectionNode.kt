package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.TypeSectionVisitor

public class TypeSectionNode : SectionNode(SectionKind.TYPE), TypeSectionVisitor {
    public val types: MutableList<FunctionTypeNode> = mutableListOf()

    public fun accept(typeSectionVisitor: TypeSectionVisitor) {
        for (functionType in types) {
            typeSectionVisitor.visitType(functionType.typeIndex, functionType.parameters, functionType.results)
        }

        typeSectionVisitor.visitEnd()
    }

    public override fun visitType(typeIndex: UInt, parameters: Array<WasmType>, results: Array<WasmType>) {
        types.add(FunctionTypeNode(typeIndex, parameters, results))
    }

    public override fun visitEnd() {
        // empty
    }
}
