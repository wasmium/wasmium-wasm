package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmSource
import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.visitors.ExportSectionVisitor
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class ExportSectionReader(
    private val context: WasmBinaryContext,
) {
    public fun readExportSection(source: WasmSource, visitor: ModuleVisitor) {
        context.numberExports = source.readVarUInt32()

        if (context.numberExports > WasmBinary.MAX_EXPORTS) {
            throw ParserException("Number of exports ${context.numberExports} exceed the maximum of ${WasmBinary.MAX_EXPORTS}")
        }

        val exportVisitor: ExportSectionVisitor = visitor.visitExportSection()
        val names = mutableSetOf<String>()
        for (exportIndex in 0u until context.numberExports) {
            val name: String = source.readInlineString()

            if (!names.add(name)) {
                throw ParserException("Duplicate export name $name")
            }

            val externalKind: ExternalKind = source.readExternalKind()
            val itemIndex: UInt = source.readIndex()

            when (externalKind) {
                ExternalKind.FUNCTION -> {
                    if (itemIndex >= context.numberTotalFunctions) {
                        throw ParserException("Invalid export function index: %$itemIndex")
                    }
                }

                ExternalKind.TABLE -> {
                    if (context.numberTotalTables == 0u) {
                        throw ParserException("Cannot index non existing table")
                    }

                    if (itemIndex != 0u) {
                        throw ParserException("Only table index 0 is supported, but using index $itemIndex")
                    }

                    if (itemIndex > context.numberTotalTables) {
                        throw ParserException("Invalid export table index: %$itemIndex")
                    }
                }

                ExternalKind.MEMORY -> {
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

                ExternalKind.GLOBAL -> {
                    if (itemIndex >= context.numberTotalGlobals) {
                        throw ParserException("Invalid export global index: %$itemIndex")
                    }
                }

                ExternalKind.EXCEPTION -> {
                    run {
                        if (!context.options.features.isExceptionHandlingEnabled) {
                            throw ParserException("Invalid export exception kind: exceptions not enabled.")
                        }
                    }
                    throw IllegalArgumentException()
                }
            }
            exportVisitor.visitExport(exportIndex, externalKind, itemIndex, name)
        }

        exportVisitor.visitEnd()
    }
}
