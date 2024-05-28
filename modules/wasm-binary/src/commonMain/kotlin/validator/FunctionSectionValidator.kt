package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.tree.TypeIndex
import org.wasmium.wasm.binary.visitors.FunctionSectionVisitor

public class FunctionSectionValidator(private val delegate: FunctionSectionVisitor? = null, private val context: ValidatorContext) : FunctionSectionVisitor {
    override fun visitFunction(typeIndex: TypeIndex) {
        val functionType = context.types.getOrElse(typeIndex.index.toInt()) {
            throw ParserException("Invalid type index at ${typeIndex.index}")
        }

        context.functions.add(functionType)

        delegate?.visitFunction(typeIndex)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
