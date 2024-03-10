package org.wasmium.wasm.binary.visitors

public class DataSectionAdapter(protected val delegate: DataSectionVisitor? = null) : DataSectionVisitor {
    public override fun visitDataSegment(segmentIndex: UInt): DataSegmentVisitor {
        if (delegate != null) {
            return DataSegmentAdapter(delegate.visitDataSegment(segmentIndex))
        }

        return DataSegmentAdapter()
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
