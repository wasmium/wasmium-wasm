package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.GlobalType.Mutability

public open class GlobalSectionAdapter(protected val delegate: GlobalSectionVisitor? = null) : GlobalSectionVisitor {

    override fun visitGlobalVariable(type: WasmType, mutability: Mutability): ExpressionVisitor = ExpressionAdapter(delegate?.visitGlobalVariable(type, mutability))

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
