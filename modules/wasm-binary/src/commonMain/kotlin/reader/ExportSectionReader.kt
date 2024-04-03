package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.ExternalKind.FUNCTION
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class ExportSectionReader(
    private val context: ReaderContext,
) {
    public fun readExportSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfExports = source.readVarUInt32()

        val exportVisitor = visitor.visitExportSection()
        val names = mutableSetOf<String>()
        for (exportIndex in 0u until context.numberOfExports) {
            val name = source.readString()
            if (!names.add(name)) {
                throw ParserException("Duplicate export name $name")
            }

            val externalKind = source.readExternalKind()

            when (externalKind) {
                FUNCTION -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid export exception kind: exceptions not enabled.")
                    }
                }

                else -> {
                    // empty
                }
            }

            val itemIndex = source.readIndex()
            exportVisitor.visitExport(name, externalKind, itemIndex)
        }

        exportVisitor.visitEnd()
    }
}
