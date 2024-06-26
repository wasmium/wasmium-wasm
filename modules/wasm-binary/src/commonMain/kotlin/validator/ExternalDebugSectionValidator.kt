package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.visitor.ExternalDebugSectionVisitor

public class ExternalDebugSectionValidator(private val delegate: ExternalDebugSectionVisitor? = null, private val context: ValidatorContext) : ExternalDebugSectionVisitor {
    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
