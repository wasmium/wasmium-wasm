package org.wasmium.wasm.binary.visitors

public open class ElementSectionAdapter(protected val delegate: ElementSectionVisitor? = null) : ElementSectionVisitor {

    public override fun visitElementSegment(): ElementSegmentVisitor = delegate?.visitElementSegment() ?: ElementSegmentAdapter()

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
