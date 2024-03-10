package org.wasmium.wasm.binary.visitors

public class ElementSegmentAdapter(protected val delegate: ElementSegmentVisitor? = null) : ElementSegmentVisitor {
    public override fun visitTableIndex(tableIndex: UInt) {
        delegate?.visitTableIndex(tableIndex)
    }

    public override fun visitInitializerExpression(): InitializerExpressionVisitor {
        if (delegate != null) {
            return InitializerExpressionAdapter(delegate.visitInitializerExpression())
        }

        return InitializerExpressionAdapter()
    }

    public override fun visitFunctionIndex(index: UInt, functionIndex: UInt) {
        delegate?.visitFunctionIndex(index, functionIndex)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
