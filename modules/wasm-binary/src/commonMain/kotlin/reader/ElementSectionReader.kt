package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class ElementSectionReader(
    private val context: ReaderContext,
    private val elementSegmentReader: ElementSegmentReader = ElementSegmentReader(context),
) {
    public fun readElementSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfElementSegments = source.readVarUInt32()

        if (context.numberOfElementSegments > WasmBinary.MAX_ELEMENT_SEGMENTS) {
            throw ParserException("Number of element segments ${context.numberOfElementSegments} exceed the maximum of ${WasmBinary.MAX_ELEMENT_SEGMENTS}")
        }

        if (context.numberOfElementSegments != 0u && context.numberOfTotalTables <= 0u) {
            throw ParserException("Element section without table section.")
        }

        val elementVisitor = visitor.visitElementSection()
        for (index in 0u until context.numberOfElementSegments) {
            elementSegmentReader.readElementSegment(source, index, elementVisitor)
        }
        elementVisitor?.visitEnd()
    }
}
