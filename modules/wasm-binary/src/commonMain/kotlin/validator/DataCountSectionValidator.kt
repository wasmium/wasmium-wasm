package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.visitor.DataCountSectionVisitor

public class DataCountSectionValidator(private val delegate: DataCountSectionVisitor? = null, private val context: ValidatorContext) : DataCountSectionVisitor {
    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
