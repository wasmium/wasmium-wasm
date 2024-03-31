package org.wasmium.wasm.binary.visitors

public interface ElementSectionVisitor {
    public fun visitElementSegment(): ElementSegmentVisitor

    public fun visitEnd()
}
