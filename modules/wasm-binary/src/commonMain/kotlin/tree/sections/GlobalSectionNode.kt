package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class GlobalSectionNode : SectionNode(SectionKind.GLOBAL), GlobalSectionVisitor {
    public val globals: MutableList<GlobalVariableNode> = mutableListOf()

    public fun accept(globalSectionVisitor: GlobalSectionVisitor) {
        for (globalVariable in globals) {
            val expressionInitializer = globalSectionVisitor.visitGlobalVariable(globalVariable.globalType.contentType, globalVariable.globalType.isMutable)
            globalVariable.initializer.accept(expressionInitializer)
        }

        globalSectionVisitor.visitEnd()
    }

    public override fun visitGlobalVariable(type: WasmType, mutable: Boolean): InitializerExpressionVisitor {
        val globalVariable = GlobalTypeNode(type, mutable)
        val initializerExpressionNode = InitializerExpressionNode()

        globals.add(GlobalVariableNode(globalVariable, initializerExpressionNode))

        return initializerExpressionNode
    }

    public override fun visitEnd() {
        // empty
    }
}
