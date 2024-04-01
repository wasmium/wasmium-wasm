package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public open class GlobalSectionAdapter(protected val delegate: GlobalSectionVisitor? = null) : GlobalSectionVisitor {

    public override fun visitGlobalVariable(type: WasmType, mutable: Boolean): InitializerExpressionVisitor =
        delegate?.visitGlobalVariable(type, mutable) ?: InitializerExpressionAdapter()

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}

