package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.TypeSectionVisitor

public class TypeSectionNode : SectionNode(SectionKind.TYPE), TypeSectionVisitor {
    public val types: MutableList<FunctionTypeNode> = mutableListOf<FunctionTypeNode>()

    public fun accept(typeSectionVisitor: TypeSectionVisitor) {
        for (functionType in types) {
            typeSectionVisitor.visitType(functionType.typeIndex!!, functionType.parameters!!, functionType.result!!)
        }
    }

    public override fun visitType(typeIndex: UInt, parameters: Array<WasmType>, results: Array<WasmType>) {
        val functionType = FunctionTypeNode()
        functionType.typeIndex = typeIndex
        functionType.parameters = parameters
        functionType.result = results

        types.add(functionType)
    }

    public override fun visitEnd() {
        // empty
    }
}
