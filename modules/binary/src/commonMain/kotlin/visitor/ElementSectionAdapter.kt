package org.wasmium.wasm.binary.visitor

public open class ElementSectionAdapter(protected val delegate: ElementSectionVisitor? = null) : ElementSectionVisitor {

    override fun visitElementSegment(): ElementSegmentVisitor = ElementSegmentAdapter(delegate?.visitElementSegment())

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
