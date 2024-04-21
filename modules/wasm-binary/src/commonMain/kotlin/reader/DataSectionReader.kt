package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class DataSectionReader(
    private val context: ReaderContext,
    private val dataSegmentReader: DataSegmentReader = DataSegmentReader(context),
) {
    public fun readDataSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val dataSegmentCount = source.readVarUInt32()

        val dataVisitor = visitor.visitDataSection()
        for (segment in 0u until dataSegmentCount) {
            dataSegmentReader.readDataSegment(source, dataVisitor)
        }

        dataVisitor.visitEnd()
    }
}
