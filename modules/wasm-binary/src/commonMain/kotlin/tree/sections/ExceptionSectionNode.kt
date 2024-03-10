package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExceptionSectionVisitor

public class ExceptionSectionNode : CustomSectionNode(), ExceptionSectionVisitor {
    public val exceptionTypes: MutableList<ExceptionTypeNode> = mutableListOf()

    public fun accept(exceptionSectionVisitor: ExceptionSectionVisitor) {
        for (exceptionType in exceptionTypes) {
            exceptionSectionVisitor.visitExceptionType(exceptionType.exceptionIndex!!, exceptionType.types!!)
        }
    }

    public override fun visitExceptionType(exceptionIndex: UInt, types: Array<WasmType>) {
        val exceptionType = ExceptionTypeNode()
        exceptionType.exceptionIndex = exceptionIndex
        exceptionType.types = types

        exceptionTypes.add(exceptionType)
    }

    public override fun visitEnd() {
        // empty
    }

}
