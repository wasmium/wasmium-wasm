package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmSource
import org.wasmium.wasm.binary.visitors.ElementSectionVisitor

public class ElementSegmentReader(
    private val context: WasmBinaryContext,
    private val initializerExpressionReader: InitializerExpressionReader = InitializerExpressionReader(context),
) {
    public fun readElementSegment(source: WasmSource, index: UInt, elementVisitor: ElementSectionVisitor) {
        val startIndex: UInt = source.position
        val elementSegmentVisitor = elementVisitor.visitElementSegment(index)

        val tableIndex: UInt = source.readIndex()
        if (tableIndex != 0u) {
            throw ParserException("Table elements must refer to table 0.")
        }
        elementSegmentVisitor.visitTableIndex(tableIndex)

        val initializerExpressionVisitor = elementSegmentVisitor.visitInitializerExpression()
        initializerExpressionReader.readInitExpression(source, initializerExpressionVisitor, true)
        initializerExpressionVisitor.visitEnd()

        val numberFunctionIndexes: UInt = source.readVarUInt32()

        if (numberFunctionIndexes + (source.position - startIndex) > WasmBinary.MAX_ELEMENT_SEGMENT_LENGTH) {
            throw ParserException(("Element segment size of ${numberFunctionIndexes + (source.position - startIndex)}") + " exceed the maximum of " + WasmBinary.MAX_ELEMENT_SEGMENT_LENGTH)
        }

        for (segmentFunctionIndex in 0u until numberFunctionIndexes) {
            val functionIndex: UInt = source.readIndex()

            elementSegmentVisitor.visitFunctionIndex(segmentFunctionIndex, functionIndex)
        }

        elementSegmentVisitor.visitEnd()
    }
}
