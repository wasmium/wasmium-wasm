package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.CodeSectionVisitor
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class CodeSectionNode : SectionNode(SectionKind.CODE), CodeSectionVisitor {
    public val functionBodies: MutableList<FunctionBodyNode> = mutableListOf()

    public fun accept(codeSectionVisitor: CodeSectionVisitor) {
        for (functionBody in functionBodies) {
            val functionBodyVisitor = codeSectionVisitor.visitFunctionBody(functionBody.functionIndex!!)

            functionBody.accept(functionBodyVisitor)

            functionBodyVisitor.visitEnd()
        }
    }

    override fun visitFunctionBody(functionIndex: UInt): FunctionBodyVisitor {
        val functionBody = FunctionBodyNode()
        functionBody.functionIndex = functionIndex
        functionBodies.add(functionBody)

        return functionBody
    }

    public override fun visitEnd() {
        // empty
    }
}
