package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.visitors.ElementSectionVisitor
import org.wasmium.wasm.binary.visitors.ElementSegmentVisitor

public class ElementSectionValidator(private val delegate: ElementSectionVisitor, private val context: ValidatorContext) : ElementSectionVisitor {
    override fun visitElementSegment(): ElementSegmentVisitor {
        return delegate.visitElementSegment()
    }

    override fun visitEnd() {
        delegate.visitEnd()
    }
}
