package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExceptionSectionVisitor

public class ExceptionSectionNode : CustomSectionNode(SectionName.EXCEPTION.sectionName), ExceptionSectionVisitor {
    public val exceptionTypes: MutableList<ExceptionTypeNode> = mutableListOf()

    public fun accept(exceptionSectionVisitor: ExceptionSectionVisitor) {
        for (exceptionType in exceptionTypes) {
            exceptionSectionVisitor.visitExceptionType(exceptionType.exceptionIndex, exceptionType.exceptionTypes)
        }

        exceptionSectionVisitor.visitEnd()
    }

    public override fun visitExceptionType(exceptionIndex: UInt, types: Array<WasmType>) {
        exceptionTypes.add(ExceptionTypeNode(exceptionIndex, types))
    }

    public override fun visitEnd() {
        // empty
    }
}
