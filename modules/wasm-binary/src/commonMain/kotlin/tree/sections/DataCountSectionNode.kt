package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.DataCountSectionVisitor

public class DataCountSectionNode : SectionNode(SectionKind.DATA_COUNT), DataCountSectionVisitor {
    public var dataCount: UInt = 0u

    public fun accept(dataCountVisitor: DataCountSectionVisitor) {
        dataCountVisitor.visitDataCount(dataCount)

        dataCountVisitor.visitEnd()
    }

    override fun visitDataCount(count: UInt) {
        this.dataCount = count
    }

    override fun visitEnd() {
        // empty
    }
}
