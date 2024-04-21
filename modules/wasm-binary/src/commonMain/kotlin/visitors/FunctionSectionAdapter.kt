package org.wasmium.wasm.binary.visitors

public open class FunctionSectionAdapter(protected val delegate: FunctionSectionVisitor? = null) : FunctionSectionVisitor {

    override fun visitFunction(typeIndex: UInt): Unit = delegate?.visitFunction(typeIndex) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
