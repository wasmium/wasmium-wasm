package org.wasmium.wasm.binary.visitors

public open class DataCountSectionAdapter(protected val delegate: DataCountSectionVisitor? = null) : DataCountSectionVisitor {
    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
