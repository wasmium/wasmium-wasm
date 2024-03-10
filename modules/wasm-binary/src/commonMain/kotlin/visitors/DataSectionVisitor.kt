package org.wasmium.wasm.binary.visitors

public interface DataSectionVisitor {
    public fun visitDataSegment(segmentIndex: UInt): DataSegmentVisitor

    public fun visitEnd()
}
