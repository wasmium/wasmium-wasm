package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.GlobalType
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.ExpressionVisitor
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor

public class GlobalSectionNode : SectionNode(SectionKind.GLOBAL), GlobalSectionVisitor {
    public val globals: MutableList<GlobalVariableNode> = mutableListOf()

    public fun accept(globalSectionVisitor: GlobalSectionVisitor) {
        for (globalVariable in globals) {
            val expressionInitializer = globalSectionVisitor.visitGlobalVariable(globalVariable.globalType)
            globalVariable.initializer.accept(expressionInitializer)
        }

        globalSectionVisitor.visitEnd()
    }

    public override fun visitGlobalVariable(globalType: GlobalType): ExpressionVisitor {
        val expressionNode = ExpressionNode()

        globals.add(GlobalVariableNode(globalType, expressionNode))

        return expressionNode
    }

    public override fun visitEnd() {
        // empty
    }
}
