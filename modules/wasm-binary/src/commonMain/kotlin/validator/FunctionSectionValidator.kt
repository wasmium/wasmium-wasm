package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.visitors.FunctionSectionVisitor

public class FunctionSectionValidator(private val delegate: FunctionSectionVisitor, private val context: ValidatorContext) : FunctionSectionVisitor {
    override fun visitFunction(signatureIndex: UInt) {
        delegate.visitFunction(signatureIndex)
    }

    override fun visitEnd() {
        delegate.visitEnd()
    }

}
