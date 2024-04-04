package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public open class GlobalSectionAdapter(protected val delegate: GlobalSectionVisitor? = null) : GlobalSectionVisitor {

    public override fun visitGlobalVariable(type: WasmType, mutable: Boolean): ExpressionVisitor =
        delegate?.visitGlobalVariable(type, mutable) ?: ExpressionAdapter()

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}

