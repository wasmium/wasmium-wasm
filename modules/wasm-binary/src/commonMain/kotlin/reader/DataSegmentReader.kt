package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmSource
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.DataSectionVisitor

public class DataSegmentReader(
    private val context: WasmBinaryContext,
    private val initializerExpressionReader: InitializerExpressionReader = InitializerExpressionReader(context)
) {
    public fun readDataSegment(source: WasmSource, index: UInt, dataVisitor: DataSectionVisitor) {
        val startIndex = source.position

        val dataSegmentVisitor = dataVisitor.visitDataSegment(index)

        val mode = source.readVarUInt32()
        when (mode) {
            0u -> {
                val initializerExpressionVisitor = dataSegmentVisitor.visitInitializerExpression()
                initializerExpressionReader.readInitExpression(source, initializerExpressionVisitor, true)
                initializerExpressionVisitor.visitEnd()

                val dataSize = source.readVarUInt32()

                if (dataSize + (source.position - startIndex) > WasmBinary.MAX_DATA_SEGMENT_LENGTH) {
                    throw ParserException("Data segment size of $dataSize${source.position - startIndex} exceed the maximum of ${WasmBinary.MAX_DATA_SEGMENT_LENGTH}")
                }

                val data = ByteArray(dataSize.toInt())
                source.readTo(data, 0u, dataSize)

                dataSegmentVisitor.visitData(data)
            }

            1u -> {
                val dataSize = source.readVarUInt32()

                if (dataSize + (source.position - startIndex) > WasmBinary.MAX_DATA_SEGMENT_LENGTH) {
                    throw ParserException("Data segment size of $dataSize${source.position - startIndex} exceed the maximum of ${WasmBinary.MAX_DATA_SEGMENT_LENGTH}")
                }

                val data = ByteArray(dataSize.toInt())
                source.readTo(data, 0u, dataSize)

                if (dataSize != source.position - startIndex) {
                    throw ParserException("Invalid size of section id: ${SectionKind.DATA}")
                }

                dataSegmentVisitor.visitData(data)
            }

            2u -> {
                val memoryIndex = source.readVarUInt32()
                if (memoryIndex != 0u) {
                    throw ParserException("Bad memory index, must be 0.")
                }

                dataSegmentVisitor.visitMemoryIndex(memoryIndex)

                val initializerExpressionVisitor = dataSegmentVisitor.visitInitializerExpression()
                initializerExpressionReader.readInitExpression(source, initializerExpressionVisitor, true)
                initializerExpressionVisitor.visitEnd()

                val dataSize = source.readVarUInt32()

                if (dataSize + (source.position - startIndex) > WasmBinary.MAX_DATA_SEGMENT_LENGTH) {
                    throw ParserException("Data segment size of $dataSize${source.position - startIndex} exceed the maximum of ${WasmBinary.MAX_DATA_SEGMENT_LENGTH}")
                }

                val data = ByteArray(dataSize.toInt())
                source.readTo(data, 0u, dataSize)

                if (dataSize != source.position - startIndex) {
                    throw ParserException("Invalid size of section id: ${SectionKind.DATA}")
                }

                dataSegmentVisitor.visitData(data)
            }

            else -> throw ParserException("Invalid mode: $mode")
        }

        dataSegmentVisitor.visitEnd()
    }
}
