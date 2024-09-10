package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.GlobalType

public open class GlobalSectionAdapter(protected val delegate: GlobalSectionVisitor? = null) : GlobalSectionVisitor {

    override fun visitGlobalVariable(globalType: GlobalType): ExpressionVisitor = ExpressionAdapter(delegate?.visitGlobalVariable(globalType))

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
