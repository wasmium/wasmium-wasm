package org.wasmium.wasm.binary.visitor

public interface DataSectionVisitor {

    public fun visitDataSegment(): DataSegmentVisitor

    public fun visitEnd()
}
