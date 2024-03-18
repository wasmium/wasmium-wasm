package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmSource
import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class ImportSectionReader(
    private val context: WasmBinaryContext,
) {
    public fun readImportSection(source: WasmSource, visitor: ModuleVisitor) {
        context.numberImports = source.readVarUInt32()

        if (context.numberImports > WasmBinary.MAX_IMPORTS) {
            throw ParserException("Number of imports ${context.numberImports} exceed the maximum of ${WasmBinary.MAX_IMPORTS}")
        }

        val importVisitor: ImportSectionVisitor = visitor.visitImportSection()
        for (importIndex in 0u until context.numberImports) {
            val moduleName = source.readInlineString()
            val fieldName = source.readInlineString()

            val externalKind: ExternalKind = source.readExternalKind()
            when (externalKind) {
                ExternalKind.FUNCTION -> {
                    val typeIndex = source.readIndex()

                    if (typeIndex >= context.numberSignatures) {
                        throw ParserException("Invalid import function index $typeIndex")
                    }

                    importVisitor.visitFunction(importIndex, moduleName, fieldName, context.numberFunctionImports, typeIndex)

                    context.numberFunctionImports++
                }

                ExternalKind.TABLE -> {
                    val elementType = source.readType()

                    if (!elementType.isElementType()) {
                        throw ParserException("Imported table type is not AnyFunc.")
                    }

                    val limits: ResizableLimits = source.readResizableLimits()
                    if (limits.isShared()) {
                        throw ParserException("Tables may not be shared")
                    }

                    importVisitor.visitTable(importIndex, moduleName, fieldName, context.numberTableImports, elementType, limits)

                    context.numberTableImports++
                }

                ExternalKind.MEMORY -> {
                    val pageLimits: ResizableLimits = source.readResizableLimits()
                    importVisitor.visitMemory(importIndex, moduleName, fieldName, context.numberMemoryImports, pageLimits)

                    context.numberMemoryImports++
                }

                ExternalKind.GLOBAL -> {
                    val globalType = source.readType()

                    if (!globalType.isValueType()) {
                        throw ParserException("Invalid global type: %#$globalType")
                    }

                    val mutable = source.readVarUInt1() == 1u
                    if (mutable) {
                        throw ParserException("Import mutate globals are not allowed")
                    }

                    importVisitor.visitGlobal(importIndex, moduleName, fieldName, context.numberGlobalImports, globalType, false)

                    context.numberGlobalImports++
                }

                ExternalKind.EXCEPTION -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid import exception kind: exceptions not enabled.")
                    }

                    val signatures = readExceptionType(source)
                    importVisitor.visitException(importIndex, moduleName, fieldName, context.numberExceptionImports, signatures)

                    context.numberExceptionImports++
                }
            }
        }

        importVisitor.visitEnd()
    }

    private fun readExceptionType(source: WasmSource): Array<WasmType> {
        val numberExceptionTypes = source.readVarUInt32()

        if (numberExceptionTypes > WasmBinary.MAX_EXCEPTION_TYPES) {
            throw ParserException("Number of exceptions types $numberExceptionTypes exceed the maximum of ${WasmBinary.MAX_EXCEPTIONS}")
        }

        val exceptionTypes = Array(numberExceptionTypes.toInt()) { WasmType.NONE }
        for (exceptionIndex in 0u until numberExceptionTypes) {
            val exceptionType = source.readType()

            if (!exceptionType.isValueType()) {
                throw ParserException("Invalid exception type: %#$exceptionType")
            }

            exceptionTypes[exceptionIndex.toInt()] = exceptionType
        }

        return exceptionTypes
    }
}
