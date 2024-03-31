package org.wasmium.wasm.binary.visitors

public open class DataSegmentAdapter(protected val delegate: DataSegmentVisitor? = null) : DataSegmentVisitor {

    public override fun visitActive(memoryIndex: UInt): InitializerExpressionVisitor {
        return InitializerExpressionAdapter()
    }

    public override fun visitData(data: ByteArray) {
        delegate?.visitData(data)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
