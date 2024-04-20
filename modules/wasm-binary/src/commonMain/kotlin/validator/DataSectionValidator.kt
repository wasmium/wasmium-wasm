package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.visitors.DataSectionVisitor
import org.wasmium.wasm.binary.visitors.DataSegmentVisitor

public class DataSectionValidator(private val delegate: DataSectionVisitor? = null, private val context: ValidatorContext) : DataSectionVisitor {
    override fun visitDataSegment(): DataSegmentVisitor {
        return DataSegmentValidator(delegate?.visitDataSegment(), context)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
