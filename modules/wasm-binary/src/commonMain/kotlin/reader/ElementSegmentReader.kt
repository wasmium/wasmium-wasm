package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinary.ELEMENT_EXPRESSIONS
import org.wasmium.wasm.binary.WasmBinary.ELEMENT_PASSIVE_OR_DECLARATIVE
import org.wasmium.wasm.binary.WasmBinary.ELEMENT_TABLE_INDEX
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.ElementKind
import org.wasmium.wasm.binary.tree.ElementKind.FUNCTION_REF
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ElementSectionVisitor

public class ElementSegmentReader(
    private val context: ReaderContext,
    private val expressionReader: ExpressionReader = ExpressionReader(context),
) {
    public fun readElementSegment(source: WasmBinaryReader, elementVisitor: ElementSectionVisitor) {
        val startIndex = source.position

        val elementSegmentVisitor = elementVisitor.visitElementSegment()

        val elementType = source.readVarUInt32()
        if (elementType >= 0x08u) {
            throw ParserException("Invalid element type $elementType")
        }

        if ((elementType and ELEMENT_PASSIVE_OR_DECLARATIVE.toUInt()) != 0u) {
            elementSegmentVisitor.visitNonActiveMode((elementType and ELEMENT_TABLE_INDEX.toUInt()) != 0u)
        } else {
            val tableIndex = if ((elementType and ELEMENT_TABLE_INDEX.toUInt()) != 0u) source.readIndex() else 0u

            val expressionVisitor = elementSegmentVisitor.visitActiveMode(tableIndex)
            expressionReader.readExpression(source, expressionVisitor)
            expressionVisitor.visitEnd()
        }

        val implicitFuncRef = (elementType and 0b011u) == 0u
        if ((elementType and ELEMENT_EXPRESSIONS.toUInt()) != 0u) {
            if (!implicitFuncRef) {
                val type = source.readWasmRefType()
                elementSegmentVisitor.visitType(type)
            }

            val initLength = source.readVarUInt32()
            (0u until initLength).forEach { _ ->
                val expressionVisitor = elementSegmentVisitor.visitExpression()
                expressionReader.readExpression(source, expressionVisitor)
                expressionVisitor.visitEnd()
            }
        } else {
            if (!implicitFuncRef) {
                val elementKindId = source.readVarUInt1()
                val elementKind = ElementKind.fromElementKind(elementKindId) ?: throw ParserException("Unrecognised element kind $elementKindId")

                val type = when (elementKind) {
                    FUNCTION_REF -> WasmType.FUNC_REF
                }
                elementSegmentVisitor.visitType(type)
            }

            val numberOfFunctionIndexes = source.readVarUInt32()
            val elementIndices = mutableListOf<UInt>()
            for (segmentFunctionIndex in 0u until numberOfFunctionIndexes) {
                elementIndices.add(source.readIndex())
            }
            elementSegmentVisitor.visitElementIndices(elementIndices)

            if (numberOfFunctionIndexes + (source.position - startIndex) > WasmBinary.MAX_ELEMENT_SEGMENT_LENGTH) {
                throw ParserException("Element segment size of ${numberOfFunctionIndexes + (source.position - startIndex)} exceed the maximum of ${WasmBinary.MAX_ELEMENT_SEGMENT_LENGTH}")
            }
        }

        elementSegmentVisitor.visitEnd()
    }
}
