package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.visitor.SourceMapSectionVisitor

public class SourceMapSectionValidator(private val delegate: SourceMapSectionVisitor? = null, private val context: ValidatorContext) : SourceMapSectionVisitor {
    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
