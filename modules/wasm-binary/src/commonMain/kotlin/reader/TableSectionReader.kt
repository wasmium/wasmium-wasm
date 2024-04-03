package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class TableSectionReader(
    private val context: ReaderContext,
) {
    public fun readTableSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfTables = source.readVarUInt32()

        val tableVisitor = visitor.visitTableSection()
        for (index in 0u until context.numberOfTables) {
            val tableIndex = context.numberOfTableImports + index

            val elementType = source.readType()
            if (elementType != WasmType.FUNC_REF) {
                throw ParserException("Table type is not AnyFunc.")
            }

            val limits = source.readResizableLimits()
            if (limits.isShared()) {
                throw ParserException("Tables may not be shared.")
            }

            if (limits.maximum != null) {
                if (limits.initial > limits.maximum) {
                    throw ParserException("Initial table page size greater than maximum")
                }
            }

            tableVisitor.visitTable(elementType, limits)
        }

        tableVisitor.visitEnd()
    }
}
