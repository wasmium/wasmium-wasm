package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.visitors.ElementSectionVisitor
import org.wasmium.wasm.binary.visitors.ElementSegmentVisitor

public class ElementSectionValidator(private val delegate: ElementSectionVisitor? = null, private val context: ValidatorContext) : ElementSectionVisitor {
    override fun visitElementSegment(): ElementSegmentVisitor {
        return ElementSegmentValidator(delegate?.visitElementSegment(), context)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
