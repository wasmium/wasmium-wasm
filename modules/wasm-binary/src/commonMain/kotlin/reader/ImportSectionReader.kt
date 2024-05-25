package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.ExternalKind.FUNCTION
import org.wasmium.wasm.binary.tree.ExternalKind.GLOBAL
import org.wasmium.wasm.binary.tree.ExternalKind.MEMORY
import org.wasmium.wasm.binary.tree.ExternalKind.TABLE
import org.wasmium.wasm.binary.tree.ExternalKind.TAG
import org.wasmium.wasm.binary.tree.GlobalType.Mutability.IMMUTABLE
import org.wasmium.wasm.binary.tree.GlobalType.Mutability.MUTABLE
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
                    val typeIndex = source.readTypeIndex()

                    importVisitor.visitFunction(moduleName, fieldName, typeIndex)

                    context.numberOfFunctionImports++
                }

                TABLE -> {
                    val elementType = source.readType()

                    if (!elementType.isReferenceType()) {
                        throw ParserException("Imported table type is not a reference type.")
                    }

                    val limits = source.readMemoryLimits()
                    if (limits.isShared()) {
                        throw ParserException("Tables may not be shared")
                    }

                    importVisitor.visitTable(moduleName, fieldName, elementType, limits)

                    context.numberOfTableImports++
                }

                MEMORY -> {
                    val memoryType = source.readMemoryType()
                    importVisitor.visitMemory(moduleName, fieldName, memoryType)

                    context.numberOfMemoryImports++
                }

                GLOBAL -> {
                    val globalType = source.readType()

                    if (!globalType.isValueType()) {
                        throw ParserException("Invalid global type: %#$globalType")
                    }

                    val mutable = if (source.readVarUInt1() == 0u) IMMUTABLE else MUTABLE
                    if (mutable == MUTABLE) {
                        throw ParserException("Import mutate globals are not allowed")
                    }

                    importVisitor.visitGlobal(moduleName, fieldName, globalType, mutable)

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
