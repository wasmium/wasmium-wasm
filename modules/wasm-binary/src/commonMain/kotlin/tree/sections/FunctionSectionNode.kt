package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.FunctionSectionVisitor

public class FunctionSectionNode : SectionNode(SectionKind.FUNCTION), FunctionSectionVisitor {
    public val types: MutableList<FunctionIndexTypeNode> = mutableListOf<FunctionIndexTypeNode>()

    public fun accept(functionSectionVisitor: FunctionSectionVisitor) {
        for (functionIndex in types) {
            functionSectionVisitor.visitFunction(functionIndex.functionIndex!!, functionIndex.typeIndex!!)
        }
    }

    override fun visitFunction(functionIndex: UInt, typeIndex: UInt) {
        val functionIndexNode = FunctionIndexTypeNode()
        functionIndexNode.functionIndex = functionIndex
        functionIndexNode.typeIndex = typeIndex

        types.add(functionIndexNode)
    }

    public override fun visitEnd() {
        // empty
    }
}
