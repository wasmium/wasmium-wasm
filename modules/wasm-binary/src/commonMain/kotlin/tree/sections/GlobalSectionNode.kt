package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor
import org.wasmium.wasm.binary.visitors.GlobalVariableVisitor

public class GlobalSectionNode : SectionNode(SectionKind.GLOBAL), GlobalSectionVisitor {
    public val globals: MutableList<GlobalVariableNode> = mutableListOf()

    public fun accept(globalSectionVisitor: GlobalSectionVisitor) {
        for (globalVariable in globals) {
            val globalVariableVisitor = globalSectionVisitor.visitGlobalVariable(globalVariable.globalIndex!!)

            globalVariable.accept(globalVariableVisitor)

            globalVariableVisitor.visitEnd()
        }
    }

    override fun visitGlobalVariable(globalIndex: UInt): GlobalVariableVisitor {
        val globalVariable = GlobalVariableNode()
        globalVariable.globalIndex = globalIndex

        globals.add(globalVariable)

        return globalVariable
    }

    public override fun visitEnd() {
        // empty
    }
}
