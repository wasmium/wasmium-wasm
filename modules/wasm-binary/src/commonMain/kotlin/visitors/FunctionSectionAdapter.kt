package org.wasmium.wasm.binary.visitors

public open class FunctionSectionAdapter(protected val delegate: FunctionSectionVisitor? = null) : FunctionSectionVisitor {
    public override fun visitFunction(typeIndex: UInt) {
        delegate?.visitFunction(typeIndex)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
