package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public open class ElementSegmentAdapter(protected val delegate: ElementSegmentVisitor? = null) : ElementSegmentVisitor {

    override fun visitElementIndices(elementIndices: List<UInt>): Unit = delegate?.visitElementIndices(elementIndices) ?: Unit

    override fun visitNonActiveMode(passive: Boolean): Unit = delegate?.visitNonActiveMode(passive) ?: Unit

    override fun visitActiveMode(tableIndex: UInt): InitializerExpressionVisitor = delegate?.visitActiveMode(tableIndex) ?: InitializerExpressionAdapter()

    override fun visitType(type: WasmType): Unit = delegate?.visitType(type) ?: Unit

    public override fun visitInitializerExpression(): InitializerExpressionVisitor = delegate?.visitInitializerExpression() ?: InitializerExpressionAdapter()

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
