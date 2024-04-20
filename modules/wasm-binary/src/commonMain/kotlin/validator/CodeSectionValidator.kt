package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.LocalVariable
import org.wasmium.wasm.binary.visitors.CodeSectionVisitor
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class CodeSectionValidator(private val delegate: CodeSectionVisitor, private val context: ValidatorContext) : CodeSectionVisitor {
    override fun visitCode(locals: List<LocalVariable>): ExpressionVisitor {
        return delegate.visitCode(locals)
    }

    override fun visitEnd() {
        delegate.visitEnd()
    }
}
