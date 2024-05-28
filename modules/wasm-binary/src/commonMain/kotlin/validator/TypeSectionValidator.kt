package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.FunctionType
import org.wasmium.wasm.binary.visitors.TypeSectionVisitor

public class TypeSectionValidator(private val delegate: TypeSectionVisitor? = null, private val context: ValidatorContext) : TypeSectionVisitor {
    override fun visitType(functionType: FunctionType) {
        context.checkFunctionType(functionType)

        context.types.add(functionType)

        delegate?.visitType(functionType)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
