package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.FunctionType
import org.wasmium.wasm.binary.visitors.FunctionSectionVisitor

public class FunctionSectionValidator(private val delegate: FunctionSectionVisitor? = null, private val context: ValidatorContext) : FunctionSectionVisitor {
    override fun visitFunction(functionType: FunctionType) {
        context.functions.add(functionType)

        delegate?.visitFunction(functionType)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
