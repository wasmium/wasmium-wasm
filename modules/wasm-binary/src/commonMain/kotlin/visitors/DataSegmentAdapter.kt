package org.wasmium.wasm.binary.visitors

public open class DataSegmentAdapter(protected val delegate: DataSegmentVisitor? = null) : DataSegmentVisitor {
    public override fun visitMode(mode: UInt) {
        delegate?.visitMode(mode)
    }

    public override fun visitMemoryData(memoryIndex: UInt, data: ByteArray) {
        delegate?.visitMemoryData(memoryIndex, data)
    }

    public override fun visitInitializerExpression(): InitializerExpressionVisitor {
        if (delegate != null) {
            return InitializerExpressionAdapter(delegate.visitInitializerExpression())
        }

        return InitializerExpressionAdapter()
    }

    public override fun visitData(data: ByteArray) {
        delegate?.visitData(data)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
