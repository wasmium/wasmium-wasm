package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class ElementSectionReader(
    private val context: ReaderContext,
    private val elementSegmentReader: ElementSegmentReader = ElementSegmentReader(context),
) {
    public fun readElementSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val numberOfElementSegments = source.readVarUInt32()

        if (numberOfElementSegments != 0u && context.numberOfTotalTables <= 0u) {
            throw ParserException("Element section without table section.")
        }

        val elementVisitor = visitor.visitElementSection()
        for (index in 0u until numberOfElementSegments) {
            elementSegmentReader.readElementSegment(source, elementVisitor)
        }
        elementVisitor.visitEnd()
    }
}
