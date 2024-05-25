package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.TypeIndex

public open class FunctionSectionAdapter(protected val delegate: FunctionSectionVisitor? = null) : FunctionSectionVisitor {

    override fun visitFunction(typeIndex: TypeIndex): Unit = delegate?.visitFunction(typeIndex) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
