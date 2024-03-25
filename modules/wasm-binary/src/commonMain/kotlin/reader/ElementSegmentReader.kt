package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ElementSectionVisitor

public class ElementSegmentReader(
    private val context: ReaderContext,
    private val initializerExpressionReader: InitializerExpressionReader = InitializerExpressionReader(context),
) {
    public fun readElementSegment(source: WasmBinaryReader, index: UInt, elementVisitor: ElementSectionVisitor?) {
        val startIndex: UInt = source.position
        val elementSegmentVisitor = elementVisitor?.visitElementSegment(index)

        val tableIndex = source.readIndex()
        if (tableIndex != 0u) {
            throw ParserException("Table elements must refer to table 0.")
        }
        elementSegmentVisitor?.visitTableIndex(tableIndex)

        val initializerExpressionVisitor = elementSegmentVisitor?.visitInitializerExpression()
        initializerExpressionReader.readInitExpression(source, initializerExpressionVisitor, true)
        initializerExpressionVisitor?.visitEnd()

        val numberFunctionIndexes = source.readVarUInt32()

        if (numberFunctionIndexes + (source.position - startIndex) > WasmBinary.MAX_ELEMENT_SEGMENT_LENGTH) {
            throw ParserException("Element segment size of ${numberFunctionIndexes + (source.position - startIndex)} exceed the maximum of ${WasmBinary.MAX_ELEMENT_SEGMENT_LENGTH}")
        }

        for (segmentFunctionIndex in 0u until numberFunctionIndexes) {
            val functionIndex = source.readIndex()

            elementSegmentVisitor?.visitFunctionIndex(segmentFunctionIndex, functionIndex)
        }

        elementSegmentVisitor?.visitEnd()
    }
}
