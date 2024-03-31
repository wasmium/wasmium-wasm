package org.wasmium.wasm.binary.visitors

public interface DataSectionVisitor {
    public fun visitDataSegment(): DataSegmentVisitor

    public fun visitEnd()
}
