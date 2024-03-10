package org.wasmium.wasm.binary.visitors

public open class ElementSectionAdapter(protected val delegate: ElementSectionVisitor? = null) : ElementSectionVisitor {
    public override fun visitElementSegment(elementIndex: UInt): ElementSegmentVisitor {
        if (delegate != null) {
            return ElementSegmentAdapter(delegate.visitElementSegment(elementIndex))
        }

        return ElementSegmentAdapter()
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
