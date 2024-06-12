package org.wasmium.wasm.binary.visitor

public interface ElementSectionVisitor {

    public fun visitElementSegment(): ElementSegmentVisitor

    public fun visitEnd()
}
