package org.wasmium.wasm.binary.visitors

public open class DataSectionAdapter(protected val delegate: DataSectionVisitor? = null) : DataSectionVisitor {

    override fun visitDataSegment(): DataSegmentVisitor = DataSegmentAdapter(delegate?.visitDataSegment())

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
