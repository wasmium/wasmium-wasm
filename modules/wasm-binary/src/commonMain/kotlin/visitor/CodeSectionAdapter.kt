package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.LocalVariable

public open class CodeSectionAdapter(protected val delegate: CodeSectionVisitor? = null) : CodeSectionVisitor {

    override fun visitCode(locals: List<LocalVariable>): ExpressionVisitor =  ExpressionAdapter(delegate?.visitCode(locals))

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
