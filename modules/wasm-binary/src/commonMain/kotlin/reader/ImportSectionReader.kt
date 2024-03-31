package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.ExternalKind.EXCEPTION
import org.wasmium.wasm.binary.tree.ExternalKind.FUNCTION
import org.wasmium.wasm.binary.tree.ExternalKind.GLOBAL
import org.wasmium.wasm.binary.tree.ExternalKind.MEMORY
import org.wasmium.wasm.binary.tree.ExternalKind.TABLE
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class ImportSectionReader(
    private val context: ReaderContext,
) {
    public fun readImportSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfImports = source.readVarUInt32()

        if (context.numberOfImports > WasmBinary.MAX_IMPORTS) {
            throw ParserException("Number of imports ${context.numberOfImports} exceed the maximum of ${WasmBinary.MAX_IMPORTS}")
        }

        val importVisitor = visitor.visitImportSection()
        for (importIndex in 0u until context.numberOfImports) {
            val moduleName = source.readString()
            val fieldName = source.readString()

            val externalKind = source.readExternalKind()
            when (externalKind) {
                FUNCTION -> {
                    val typeIndex = source.readIndex()

                    if (typeIndex >= context.numberOfSignatures) {
                        throw ParserException("Invalid import function index $typeIndex")
                    }

                    importVisitor?.visitFunction(moduleName, fieldName, typeIndex)

                    context.numberOfFunctionImports++
                }

                TABLE -> {
                    val elementType = source.readType()

                    if (!elementType.isElementType()) {
                        throw ParserException("Imported table type is not AnyFunc.")
                    }

                    val limits = source.readResizableLimits()
                    if (limits.isShared()) {
                        throw ParserException("Tables may not be shared")
                    }

                    importVisitor?.visitTable(moduleName, fieldName, elementType, limits)

                    context.numberOfTableImports++
                }

                MEMORY -> {
                    val pageLimits = source.readResizableLimits()
                    importVisitor?.visitMemory(moduleName, fieldName, pageLimits)

                    context.numberOfMemoryImports++
                }

                GLOBAL -> {
                    val globalType = source.readType()

                    if (!globalType.isValueType()) {
                        throw ParserException("Invalid global type: %#$globalType")
                    }

                    val mutable = source.readVarUInt1() == 1u
                    if (mutable) {
                        throw ParserException("Import mutate globals are not allowed")
                    }

                    importVisitor?.visitGlobal(moduleName, fieldName, globalType, false)

                    context.numberOfGlobalImports++
                }

                EXCEPTION -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid import exception kind: exceptions not enabled.")
                    }

                    val signatures = readExceptionType(source)
                    importVisitor?.visitException(moduleName, fieldName, signatures)

                    context.numberOfExceptionImports++
                }
            }
        }

        importVisitor?.visitEnd()
    }

    private fun readExceptionType(source: WasmBinaryReader): List<WasmType> {
        val numberOfExceptionTypes = source.readVarUInt32()

        if (numberOfExceptionTypes > WasmBinary.MAX_EXCEPTION_TYPES) {
            throw ParserException("Number of exceptions types $numberOfExceptionTypes exceed the maximum of ${WasmBinary.MAX_EXCEPTIONS}")
        }

        val exceptionTypes = mutableListOf<WasmType>()
        for (exceptionIndex in 0u until numberOfExceptionTypes) {
            val exceptionType = source.readType()

            if (!exceptionType.isValueType()) {
                throw ParserException("Invalid exception type: %#$exceptionType")
            }

            exceptionTypes[exceptionIndex.toInt()] = exceptionType
        }

        return exceptionTypes
    }
}
