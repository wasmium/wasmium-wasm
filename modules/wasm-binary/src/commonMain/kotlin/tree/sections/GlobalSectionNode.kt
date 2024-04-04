package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExpressionVisitor
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor

public class GlobalSectionNode : SectionNode(SectionKind.GLOBAL), GlobalSectionVisitor {
    public val globals: MutableList<GlobalVariableNode> = mutableListOf()

    public fun accept(globalSectionVisitor: GlobalSectionVisitor) {
        for (globalVariable in globals) {
            val expressionInitializer = globalSectionVisitor.visitGlobalVariable(globalVariable.globalType.contentType, globalVariable.globalType.isMutable)
            globalVariable.initializer.accept(expressionInitializer)
        }

        globalSectionVisitor.visitEnd()
    }

    public override fun visitGlobalVariable(type: WasmType, mutable: Boolean): ExpressionVisitor {
        val globalVariable = GlobalTypeNode(type, mutable)
        val expressionNode = ExpressionNode()

        globals.add(GlobalVariableNode(globalVariable, expressionNode))

        return expressionNode
    }

    public override fun visitEnd() {
        // empty
    }
}
