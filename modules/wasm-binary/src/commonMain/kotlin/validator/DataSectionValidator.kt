package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.visitors.DataSectionVisitor
import org.wasmium.wasm.binary.visitors.DataSegmentVisitor

public class DataSectionValidator(private val delegate: DataSectionVisitor, private val context: ValidatorContext) : DataSectionVisitor {
    override fun visitDataSegment(): DataSegmentVisitor {
        return delegate.visitDataSegment()
    }

    override fun visitEnd() {
        delegate.visitEnd()
    }
}
