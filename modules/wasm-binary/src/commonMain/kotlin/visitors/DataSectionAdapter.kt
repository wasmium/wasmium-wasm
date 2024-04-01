package org.wasmium.wasm.binary.visitors

public open class DataSectionAdapter(protected val delegate: DataSectionVisitor? = null) : DataSectionVisitor {

    public override fun visitDataSegment(): DataSegmentVisitor = delegate?.visitDataSegment() ?: DataSegmentAdapter()

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
