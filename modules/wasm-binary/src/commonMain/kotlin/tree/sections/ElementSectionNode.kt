package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitor.ElementSectionVisitor
import org.wasmium.wasm.binary.visitor.ElementSegmentVisitor

public class ElementSectionNode : SectionNode(SectionKind.ELEMENT), ElementSectionVisitor {
    public val segments: MutableList<ElementSegmentNode> = mutableListOf()

    public fun accept(elementSectionVisitor: ElementSectionVisitor) {
        for (elementSegment in segments) {
            val elementSegmentVisitor = elementSectionVisitor.visitElementSegment()
            elementSegment.accept(elementSegmentVisitor)
        }

        elementSectionVisitor.visitEnd()
    }

    public override fun visitElementSegment(): ElementSegmentVisitor {
        val elementSegment = ElementSegmentNode()
        segments.add(elementSegment)
        return elementSegment
    }

    public override fun visitEnd() {
        // empty
    }
}
