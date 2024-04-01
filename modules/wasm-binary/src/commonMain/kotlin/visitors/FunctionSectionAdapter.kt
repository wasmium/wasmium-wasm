package org.wasmium.wasm.binary.visitors

public open class FunctionSectionAdapter(protected val delegate: FunctionSectionVisitor? = null) : FunctionSectionVisitor {

    public override fun visitFunction(typeIndex: UInt): Unit = delegate?.visitFunction(typeIndex) ?: Unit

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
