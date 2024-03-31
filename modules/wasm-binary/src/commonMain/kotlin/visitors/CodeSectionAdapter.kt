package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.LocalVariable

public open class CodeSectionAdapter(protected val delegate: CodeSectionVisitor? = null) : CodeSectionVisitor {
    public override fun visitCode(locals: List<LocalVariable>): ExpressionVisitor {
        return delegate?.visitCode(locals) ?: ExpressionAdapter()
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
