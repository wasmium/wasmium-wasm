package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitor.DataSectionVisitor
import org.wasmium.wasm.binary.visitor.DataSegmentVisitor

public class DataSectionNode : SectionNode(SectionKind.DATA), DataSectionVisitor {
    public val segments: MutableList<DataSegmentNode> = mutableListOf()

    public fun accept(dataSectionVisitor: DataSectionVisitor) {
        for (segment in segments) {
            val dataSegmentVisitor = dataSectionVisitor.visitDataSegment()
            segment.accept(dataSegmentVisitor)
        }

        dataSectionVisitor.visitEnd()
    }

    public override fun visitDataSegment(): DataSegmentVisitor {
        val dataSegment = DataSegmentNode()
        segments.add(dataSegment)

        return dataSegment
    }

    public override fun visitEnd() {
        // empty
    }
}
