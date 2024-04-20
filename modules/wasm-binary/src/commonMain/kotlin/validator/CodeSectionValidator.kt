package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.LocalVariable
import org.wasmium.wasm.binary.visitors.CodeSectionVisitor
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class CodeSectionValidator(private val delegate: CodeSectionVisitor? = null, private val context: ValidatorContext) : CodeSectionVisitor {
    override fun visitCode(locals: List<LocalVariable>): ExpressionVisitor {
        return ExpressionValidator(delegate?.visitCode(locals), context)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
