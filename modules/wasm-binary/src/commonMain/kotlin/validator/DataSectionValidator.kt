package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.visitor.DataSectionVisitor
import org.wasmium.wasm.binary.visitor.DataSegmentVisitor

public class DataSectionValidator(private val delegate: DataSectionVisitor? = null, private val context: ValidatorContext) : DataSectionVisitor {
    override fun visitDataSegment(): DataSegmentVisitor {
        return DataSegmentValidator(delegate?.visitDataSegment(), context)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
