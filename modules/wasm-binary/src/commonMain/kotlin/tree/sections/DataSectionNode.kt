package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.DataSectionVisitor
import org.wasmium.wasm.binary.visitors.DataSegmentVisitor

public class DataSectionNode : SectionNode(SectionKind.DATA), DataSectionVisitor {
    public val segments: MutableList<DataSegmentNode> = mutableListOf()

    public fun accept(dataSectionVisitor: DataSectionVisitor) {
        for (dataSegment in segments) {
            val dataSegmentVisitor = dataSectionVisitor.visitDataSegment(dataSegment.segmentIndex!!)

            dataSegment.accept(dataSegmentVisitor)

            dataSegmentVisitor.visitEnd()
        }
    }

    override fun visitDataSegment(segmentIndex: UInt): DataSegmentVisitor {
        val dataSegment = DataSegmentNode()
        dataSegment.segmentIndex = segmentIndex

        segments.add(dataSegment)

        return dataSegment
    }

    public override fun visitEnd() {
        // empty
    }
}
