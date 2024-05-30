package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public open class ElementSegmentAdapter(protected val delegate: ElementSegmentVisitor? = null) : ElementSegmentVisitor {

    override fun visitElementIndices(elementIndices: List<UInt>): Unit = delegate?.visitElementIndices(elementIndices) ?: Unit

    override fun visitNonActiveMode(passive: Boolean): Unit = delegate?.visitNonActiveMode(passive) ?: Unit

    override fun visitActiveMode(tableIndex: UInt): ExpressionVisitor = ExpressionAdapter(delegate?.visitActiveMode(tableIndex))

    override fun visitType(type: WasmType): Unit = delegate?.visitType(type) ?: Unit

    override fun visitExpression(): ExpressionVisitor = ExpressionAdapter(delegate?.visitExpression())

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
