package org.wasmium.wasm.binary.visitors

public class DataSegmentAdapter(protected val delegate: DataSegmentVisitor? = null) : DataSegmentVisitor {
    public override fun visitMemoryIndex(memoryIndex: UInt) {
        delegate?.visitMemoryIndex(memoryIndex)
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
