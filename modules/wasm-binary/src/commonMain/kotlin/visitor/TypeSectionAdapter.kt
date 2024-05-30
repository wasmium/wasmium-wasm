package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.FunctionType

public open class TypeSectionAdapter(protected val delegate: TypeSectionVisitor? = null) : TypeSectionVisitor {

    override fun visitType(functionType: FunctionType): Unit = delegate?.visitType(functionType) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
