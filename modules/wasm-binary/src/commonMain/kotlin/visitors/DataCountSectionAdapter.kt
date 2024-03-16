package org.wasmium.wasm.binary.visitors

public open class DataCountSectionAdapter(protected val delegate: DataCountSectionVisitor? = null) : DataCountSectionVisitor {
    public override fun visitDataCount(count: UInt) {
        delegate?.visitDataCount(count)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
