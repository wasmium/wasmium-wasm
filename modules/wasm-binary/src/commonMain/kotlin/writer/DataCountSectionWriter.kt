package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.tree.sections.DataCountSectionNode
import org.wasmium.wasm.binary.visitors.DataCountSectionAdapter

public class DataCountSectionWriter(private val context: WriterBinaryContext) : DataCountSectionAdapter() {
    public override fun visitDataCount(count: UInt) {
        if (context.startSection != null) {
            throw ParserException("Data count section already defined")
        }

        val dataCountSection = DataCountSectionNode()
        dataCountSection.dataCount = count
        context.dataCountSection = dataCountSection
    }

    public override fun visitEnd() {
        // empty
    }
}
