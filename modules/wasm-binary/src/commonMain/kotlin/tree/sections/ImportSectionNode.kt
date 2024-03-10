package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor
import org.wasmium.wasm.binary.tree.ResizableLimits

public class ImportSectionNode : SectionNode(SectionKind.IMPORT), ImportSectionVisitor {
    public val imports: MutableList<ImportNode> = mutableListOf<ImportNode>()

    public fun accept(importSectionVisitor: ImportSectionVisitor) {
        for (importNode in imports) {
            when (importNode.externalKind) {
                ExternalKind.FUNCTION -> {
                    val functionImport = importNode as FunctionImportNode
                    importSectionVisitor.visitFunction(
                        functionImport.importIndex!!,
                        functionImport.module!!,
                        functionImport.field!!,
                        functionImport.functionIndex!!,
                        functionImport.typeIndex!!,
                    )
                }

                ExternalKind.TABLE -> {
                    val tableImport: TableImportNode = importNode as TableImportNode
                    val tableType: TableTypeNode = tableImport.tableType!!
                    importSectionVisitor.visitTable(
                        importNode.importIndex!!,
                        tableImport.module!!,
                        tableImport.field!!,
                        tableImport.tableIndex!!,
                        tableType.elementType!!,
                        tableType.limits!!
                    )
                }

                ExternalKind.MEMORY -> {
                    val memoryImport: MemoryImportNode = importNode as MemoryImportNode
                    importSectionVisitor.visitMemory(
                        importNode.importIndex!!,
                        memoryImport.module!!,
                        memoryImport.field!!,
                        memoryImport.memoryIndex!!,
                        memoryImport.memoryType!!.limits!!
                    )
                }

                ExternalKind.GLOBAL -> {
                    val globalImport = importNode as GlobalImportNode
                    val globalType = globalImport.globalType
                    importSectionVisitor.visitGlobal(
                        importNode.importIndex!!,
                        globalImport.module!!,
                        globalImport.field!!,
                        globalImport.globalIndex!!,
                        globalType!!.contentType!!,
                        globalType.isMutable,
                    )
                }

                ExternalKind.EXCEPTION -> {
                    val exceptionImport = importNode as ExceptionImportNode
                    val exceptionType = exceptionImport.exceptionType
                    importSectionVisitor.visitException(
                        importNode.importIndex!!,
                        exceptionImport.module!!,
                        exceptionImport.field!!,
                        exceptionImport.exceptionIndex!!,
                        exceptionType!!.types!!,
                    )
                }

                else -> throw IllegalArgumentException()
            }
        }
    }

    public override fun visitFunction(importIndex: UInt, moduleName: String, fieldName: String, functionIndex: UInt, typeIndex: UInt) {
        val functionImport = FunctionImportNode()
        functionImport.functionIndex = importIndex
        functionImport.module = moduleName
        functionImport.field = fieldName
        functionImport.functionIndex = importIndex
        functionImport.typeIndex = typeIndex

        imports.add(functionImport)
    }

    public override fun visitGlobal(importIndex: UInt, moduleName: String, fieldName: String, globalIndex: UInt, type: WasmType, mutable: Boolean) {
        val globalType = GlobalTypeNode()
        globalType.contentType = type
        globalType.isMutable = mutable

        val globalImport = GlobalImportNode()
        globalImport.importIndex = importIndex
        globalImport.module = moduleName
        globalImport.field = fieldName
        globalImport.globalIndex = globalIndex
        globalImport.globalType = globalType

        imports.add(globalImport)
    }

    public override fun visitTable(importIndex: UInt, moduleName: String, fieldName: String, tableIndex: UInt, elementType: WasmType, limits: ResizableLimits) {
        val tableType: TableTypeNode = TableTypeNode()
        tableType.elementType = elementType
        tableType.limits = limits

        val tableImport: TableImportNode = TableImportNode()
        tableImport.importIndex = importIndex
        tableImport.module = moduleName
        tableImport.field = fieldName
        tableImport.tableIndex = tableIndex
        tableImport.tableType = tableType

        imports.add(tableImport)
    }

    public override fun visitMemory(importIndex: UInt, moduleName: String, fieldName: String, memoryIndex: UInt, limits: ResizableLimits) {
        val memoryType: MemoryTypeNode = MemoryTypeNode()
        memoryType.limits = limits

        val memoryImport: MemoryImportNode = MemoryImportNode()
        memoryImport.importIndex = importIndex
        memoryImport.module = moduleName
        memoryImport.field = fieldName
        memoryImport.memoryIndex = memoryIndex
        memoryImport.memoryType = memoryType

        imports.add(memoryImport)
    }

    public override fun visitException(importIndex: UInt, moduleName: String, fieldName: String, exceptionIndex: UInt, types: Array<WasmType>) {
        val exceptionType = ExceptionTypeNode()
        exceptionType.types = types

        val exceptionImport = ExceptionImportNode()
        exceptionImport.importIndex = importIndex
        exceptionImport.module = moduleName
        exceptionImport.field = fieldName
        exceptionImport.exceptionIndex = exceptionIndex
        exceptionImport.exceptionType = exceptionType

        imports.add(exceptionImport)
    }

    public override fun visitEnd() {
        // empty
    }
}
