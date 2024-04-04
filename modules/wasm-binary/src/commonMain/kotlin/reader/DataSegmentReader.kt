package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.DataSectionVisitor

public class DataSegmentReader(
    private val context: ReaderContext,
    private val expressionReader: ExpressionReader = ExpressionReader(context)
) {
    public fun readDataSegment(source: WasmBinaryReader, dataVisitor: DataSectionVisitor) {
        val startIndex = source.position

        val dataSegmentVisitor = dataVisitor.visitDataSegment()

        val dataType = source.readVarUInt32()
        if (dataType and WasmBinary.DATA_PASSIVE.toUInt() == 0u) {
            val memoryIndex = if ((dataType and WasmBinary.DATA_EXPLICIT.toUInt()) == 0u) 0u else source.readVarUInt32()

            val expressionVisitor = dataSegmentVisitor.visitActive(memoryIndex)
            expressionReader.readExpression(source, expressionVisitor)
            expressionVisitor.visitEnd()
        }

        val dataSize = source.readVarUInt32()
        if (dataSize + (source.position - startIndex) > WasmBinary.MAX_DATA_SEGMENT_LENGTH) {
            throw ParserException("Data segment size of $dataSize${source.position - startIndex} exceed the maximum of ${WasmBinary.MAX_DATA_SEGMENT_LENGTH}")
        }
        val data = ByteArray(dataSize.toInt())
        source.readTo(data, 0u, dataSize)
        dataSegmentVisitor.visitData(data)
    }
}
