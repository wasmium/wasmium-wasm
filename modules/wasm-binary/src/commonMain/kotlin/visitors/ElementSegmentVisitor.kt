package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public interface ElementSegmentVisitor {
    public fun visitElementIndices(elementIndices: List<UInt>)

    public fun visitNonActiveMode(passive: Boolean)

    public fun visitActiveMode(tableIndex: UInt): InitializerExpressionVisitor

    public fun visitType(type: WasmType)

    public fun visitInitializerExpression(): InitializerExpressionVisitor

    public fun visitEnd()
}
