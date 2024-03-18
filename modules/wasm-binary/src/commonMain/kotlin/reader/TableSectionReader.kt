package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmSource
import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ModuleVisitor
import org.wasmium.wasm.binary.visitors.TableSectionVisitor

public class TableSectionReader(
    private val context: WasmBinaryContext,
) {
    public fun readTableSection(source: WasmSource, visitor: ModuleVisitor) {
        context.numberTables = source.readVarUInt32()

        if (context.numberTables > WasmBinary.MAX_TABLES) {
            throw ParserException("Number of tables ${context.numberTables} exceed the maximum of ${WasmBinary.MAX_TABLES}")
        }

        val tableVisitor: TableSectionVisitor = visitor.visitTableSection()
        for (index in 0u until context.numberTables) {
            val tableIndex = context.numberTableImports + index

            val elementType: WasmType = source.readType()
            if (elementType != WasmType.FUNC_REF) {
                throw ParserException("Table type is not AnyFunc.")
            }

            val limits: ResizableLimits = source.readResizableLimits()
            if (limits.isShared()) {
                throw ParserException("Tables may not be shared.")
            }

            if (limits.initial > WasmBinary.MAX_TABLE_PAGES) {
                throw ParserException("Invalid initial memory pages")
            }

            if (limits.maximum != null) {
                if (limits.maximum > WasmBinary.MAX_TABLE_PAGES) {
                    throw ParserException("Invalid table max page")
                }

                if (limits.initial > limits.maximum) {
                    throw ParserException("Initial table page size greater than maximum")
                }
            }

            tableVisitor.visitTable(tableIndex, elementType, limits)
        }

        tableVisitor.visitEnd()
    }
}
