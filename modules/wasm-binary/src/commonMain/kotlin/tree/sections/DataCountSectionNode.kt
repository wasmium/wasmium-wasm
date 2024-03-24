package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.DataCountSectionVisitor

public class DataCountSectionNode(
    public val dataCount: UInt
) : SectionNode(SectionKind.DATA_COUNT), DataCountSectionVisitor {
    public fun accept(dataCountVisitor: DataCountSectionVisitor) {
        dataCountVisitor.visitEnd()
    }

    public override fun visitEnd() {
        // empty
    }
}
