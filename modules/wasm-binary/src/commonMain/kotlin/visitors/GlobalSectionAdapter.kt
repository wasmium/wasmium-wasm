package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public open class GlobalSectionAdapter(protected val delegate: GlobalSectionVisitor? = null) : GlobalSectionVisitor {

    override fun visitGlobalVariable(type: WasmType, mutable: Boolean): ExpressionVisitor = ExpressionAdapter(delegate?.visitGlobalVariable(type, mutable))

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
