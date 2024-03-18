package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.tree.sections.DataCountSectionNode
import org.wasmium.wasm.binary.visitors.DataCountSectionAdapter

public class DataCountSectionWriter(private val context: WriterContext) : DataCountSectionAdapter() {
    init {
        this.context.dataCountSection = DataCountSectionNode()
    }

    public override fun visitDataCount(count: UInt) {
        val dataCountSection = DataCountSectionNode()
        dataCountSection.dataCount = count
        context.dataCountSection = dataCountSection
    }

    public override fun visitEnd() {
        // empty
    }
}
