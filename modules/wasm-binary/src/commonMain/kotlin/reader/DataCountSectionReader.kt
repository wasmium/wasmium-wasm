package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class DataCountSectionReader(private val context: ReaderContext) {
    public fun readDataCountSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val dataCount = source.readVarUInt32()

        val dataCountSectionVisitor = visitor.visitDataCountSection(dataCount)
        dataCountSectionVisitor.visitEnd()
    }
}
