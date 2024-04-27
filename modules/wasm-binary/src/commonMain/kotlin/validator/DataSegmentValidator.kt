package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.DataSegmentVisitor
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class DataSegmentValidator(private val delegate: DataSegmentVisitor? = null, private val context: ValidatorContext) : DataSegmentVisitor {
    override fun visitActive(memoryIndex: UInt): ExpressionVisitor {
        val localContext = context.createLocalContext(emptyList(), emptyList())

        return ExpressionValidator(delegate?.visitActive(memoryIndex), localContext, listOf(WasmType.I32))
    }

    override fun visitData(data: ByteArray) {
        delegate?.visitData(data)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
