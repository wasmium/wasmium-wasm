package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.visitors.StartSectionVisitor

public class StartSectionValidator(private val delegate: StartSectionVisitor, private val context: ValidatorContext) : StartSectionVisitor {
    override fun visitEnd() {
        delegate.visitEnd()
    }
}
