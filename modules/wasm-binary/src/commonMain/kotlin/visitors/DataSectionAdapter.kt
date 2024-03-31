package org.wasmium.wasm.binary.visitors

public open class DataSectionAdapter(protected val delegate: DataSectionVisitor? = null) : DataSectionVisitor {

    public override fun visitDataSegment(): DataSegmentVisitor {
        return delegate?.visitDataSegment() ?: DataSegmentAdapter()
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
