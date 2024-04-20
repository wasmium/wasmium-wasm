package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.visitors.UnknownSectionVisitor

public class UnknownSectionValidator(private val delegate: UnknownSectionVisitor? = null, private val context: ValidatorContext) : UnknownSectionVisitor {
    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
