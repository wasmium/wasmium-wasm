package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.visitors.DataSegmentVisitor
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class DataSegmentValidator(private val delegate: DataSegmentVisitor? = null, private val context: ValidatorContext) : DataSegmentVisitor {
    override fun visitActive(memoryIndex: UInt): ExpressionVisitor {
        return ExpressionValidator(delegate?.visitActive(memoryIndex), context)
    }

    override fun visitData(data: ByteArray) {
        delegate?.visitData(data)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
