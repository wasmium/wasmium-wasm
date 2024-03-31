package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public open class ElementSegmentAdapter(protected val delegate: ElementSegmentVisitor? = null) : ElementSegmentVisitor {

    override fun visitElementIndices(elementIndices: List<UInt>) {
        delegate?.visitElementIndices(elementIndices)
    }

    override fun visitNonActiveMode(passive: Boolean) {
        delegate?.visitNonActiveMode(passive)
    }

    override fun visitActiveMode(tableIndex: UInt): InitializerExpressionVisitor {
        return InitializerExpressionAdapter(delegate?.visitActiveMode(tableIndex))
    }

    override fun visitType(type: WasmType) {
        delegate?.visitType(type)
    }

    public override fun visitInitializerExpression(): InitializerExpressionVisitor {
        return InitializerExpressionAdapter(delegate?.visitInitializerExpression())
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
