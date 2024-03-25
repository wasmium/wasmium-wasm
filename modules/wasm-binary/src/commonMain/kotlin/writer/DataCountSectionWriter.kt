package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.tree.sections.DataCountSectionNode
import org.wasmium.wasm.binary.visitors.DataCountSectionAdapter

public class DataCountSectionWriter(
    private val context: WriterContext,
    private val dataCount: UInt,
) : DataCountSectionAdapter() {
    public override fun visitEnd() {
        context.dataCountSection = DataCountSectionNode(dataCount)
    }
}
