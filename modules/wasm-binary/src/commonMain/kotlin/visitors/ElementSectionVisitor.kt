package org.wasmium.wasm.binary.visitors

public interface ElementSectionVisitor {
    public fun visitElementSegment(elementIndex: UInt): ElementSegmentVisitor

    public fun visitEnd()
}
