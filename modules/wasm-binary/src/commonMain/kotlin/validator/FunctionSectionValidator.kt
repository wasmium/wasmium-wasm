package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.visitors.FunctionSectionVisitor

public class FunctionSectionValidator(private val delegate: FunctionSectionVisitor? = null, private val context: ValidatorContext) : FunctionSectionVisitor {
    override fun visitFunction(typeIndex: UInt) {
        context.functions.add(context.resultType(typeIndex))

        delegate?.visitFunction(typeIndex)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }

}
