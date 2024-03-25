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
        context.numberElementSegments = source.readVarUInt32()

        if (context.numberElementSegments > WasmBinary.MAX_ELEMENT_SEGMENTS) {
            throw ParserException("Number of element segments ${context.numberElementSegments} exceed the maximum of ${WasmBinary.MAX_ELEMENT_SEGMENTS}")
        }

        if (context.numberElementSegments != 0u && context.numberTotalTables <= 0u) {
            throw ParserException("Element section without table section.")
        }

        val elementVisitor = visitor.visitElementSection()
        for (index in 0u until context.numberElementSegments) {
            elementSegmentReader.readElementSegment(source, index, elementVisitor)
        }

        elementVisitor?.visitEnd()
    }
}
