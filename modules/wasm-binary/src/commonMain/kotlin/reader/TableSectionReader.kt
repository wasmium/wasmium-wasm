package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class TableSectionReader(
    private val context: ReaderContext,
) {
    public fun readTableSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfTables = source.readVarUInt32()

        val tableVisitor = visitor.visitTableSection()
        for (index in 0u until context.numberOfTables) {
            val tableType = source.readTableType()

            tableVisitor.visitTable(tableType)
        }

        tableVisitor.visitEnd()
    }
}
