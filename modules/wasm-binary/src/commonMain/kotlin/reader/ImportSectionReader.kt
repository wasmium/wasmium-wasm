package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.ExternalKind.FUNCTION
import org.wasmium.wasm.binary.tree.ExternalKind.GLOBAL
import org.wasmium.wasm.binary.tree.ExternalKind.MEMORY
import org.wasmium.wasm.binary.tree.ExternalKind.TABLE
import org.wasmium.wasm.binary.tree.ExternalKind.TAG
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class ImportSectionReader(
    private val context: ReaderContext,
) {
    public fun readImportSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfImports = source.readVarUInt32()

        val importVisitor = visitor.visitImportSection()
        for (importIndex in 0u until context.numberOfImports) {
            val moduleName = source.readString()
            val fieldName = source.readString()

            val externalKind = source.readExternalKind()
            when (externalKind) {
                FUNCTION -> {
                    val typeIndex = source.readIndex()

                    val functionType = context.functionTypes.getOrElse(typeIndex.toInt()) {
                        throw ParserException("Invalid type index at ${typeIndex}")
                    }

                    importVisitor.visitFunction(moduleName, fieldName, functionType)

                    context.numberOfFunctionImports++
                }

                TABLE -> {
                    val tableType = source.readTableType()

                    importVisitor.visitTable(moduleName, fieldName, tableType)

                    context.numberOfTableImports++
                }

                MEMORY -> {
                    val memoryType = source.readMemoryType()
                    importVisitor.visitMemory(moduleName, fieldName, memoryType)

                    context.numberOfMemoryImports++
                }

                GLOBAL -> {
                    val globalType = source.readGlobalType()

                    importVisitor.visitGlobal(moduleName, fieldName, globalType)

                    context.numberOfGlobalImports++
                }

                TAG -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid import exception kind: exceptions not enabled.")
                    }

                    val tagType = source.readTagType()
                    importVisitor.visitTag(moduleName, fieldName, tagType)

                    context.numberOfTagImports++
                }
            }
        }

        importVisitor.visitEnd()
    }
}
