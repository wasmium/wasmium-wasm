package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.ExternalKind.EXCEPTION
import org.wasmium.wasm.binary.tree.ExternalKind.FUNCTION
import org.wasmium.wasm.binary.tree.ExternalKind.GLOBAL
import org.wasmium.wasm.binary.tree.ExternalKind.MEMORY
import org.wasmium.wasm.binary.tree.ExternalKind.TABLE
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class ExportSectionReader(
    private val context: ReaderContext,
) {
    public fun readExportSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfExports = source.readVarUInt32()

        if (context.numberOfExports > WasmBinary.MAX_EXPORTS) {
            throw ParserException("Number of exports ${context.numberOfExports} exceed the maximum of ${WasmBinary.MAX_EXPORTS}")
        }

        val exportVisitor = visitor.visitExportSection()
        val names = mutableSetOf<String>()
        for (exportIndex in 0u until context.numberOfExports) {
            val name = source.readString()

            if (!names.add(name)) {
                throw ParserException("Duplicate export name $name")
            }

            val externalKind = source.readExternalKind()
            val itemIndex = source.readIndex()

            when (externalKind) {
                FUNCTION -> {
                    if (itemIndex >= context.numberOfTotalFunctions) {
                        throw ParserException("Invalid export function index: %$itemIndex")
                    }
                }

                TABLE -> {
                    if (context.numberOfTotalTables == 0u) {
                        throw ParserException("Cannot index non existing table")
                    }

                    if (itemIndex != 0u) {
                        throw ParserException("Only table index 0 is supported, but using index $itemIndex")
                    }

                    if (itemIndex > context.numberOfTotalTables) {
                        throw ParserException("Invalid export table index: %$itemIndex")
                    }
                }

                MEMORY -> {
                    if (context.numberTotalMemories == 0u) {
                        throw ParserException("Cannot index non existing memory")
                    }

                    if (itemIndex != 0u) {
                        throw ParserException("Only memory index 0 is supported, but using index $itemIndex")
                    }

                    if (itemIndex > context.numberTotalMemories) {
                        throw ParserException("Invalid export memories index: %$itemIndex")
                    }
                }

                GLOBAL -> {
                    if (itemIndex >= context.numberOfTotalGlobals) {
                        throw ParserException("Invalid export global index: %$itemIndex")
                    }
                }

                EXCEPTION -> {
                    run {
                        if (!context.options.features.isExceptionHandlingEnabled) {
                            throw ParserException("Invalid export exception kind: exceptions not enabled.")
                        }
                    }
                    throw IllegalArgumentException()
                }
            }
            exportVisitor?.visitExport(name, externalKind, itemIndex)
        }

        exportVisitor?.visitEnd()
    }
}
