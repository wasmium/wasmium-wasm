package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.FunctionType

public open class FunctionSectionAdapter(protected val delegate: FunctionSectionVisitor? = null) : FunctionSectionVisitor {

    override fun visitFunction(functionType: FunctionType): Unit = delegate?.visitFunction(functionType) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
