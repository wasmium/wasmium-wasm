package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ElementSegmentVisitor
import org.wasmium.wasm.binary.visitors.ExpressionAdapter
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class ElementSegmentValidator(private val delegate: ElementSegmentVisitor? = null, private val context: ValidatorContext) : ElementSegmentVisitor {
    override fun visitElementIndices(elementIndices: List<UInt>) {
        delegate?.visitElementIndices(elementIndices)
    }

    override fun visitNonActiveMode(passive: Boolean) {
        delegate?.visitNonActiveMode(passive)
    }

    override fun visitActiveMode(tableIndex: UInt): ExpressionVisitor {
        return ExpressionAdapter(delegate?.visitActiveMode(tableIndex))
    }

    override fun visitType(type: WasmType) {
        delegate?.visitType(type)
    }

    override fun visitExpression(): ExpressionVisitor {
        return ExpressionAdapter(delegate?.visitExpression())
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
