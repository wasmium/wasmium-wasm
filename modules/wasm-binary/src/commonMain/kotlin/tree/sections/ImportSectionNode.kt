package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class ImportSectionNode : SectionNode(SectionKind.IMPORT), ImportSectionVisitor {
    public val imports: MutableList<ImportNode> = mutableListOf()

    public fun accept(importSectionVisitor: ImportSectionVisitor) {
        for (import in imports) {
            import.accept(importSectionVisitor)
        }

        importSectionVisitor.visitEnd()
    }

    public override fun visitFunction(importIndex: UInt, moduleName: String, fieldName: String, functionIndex: UInt, typeIndex: UInt) {
        imports.add(FunctionImportNode(importIndex, moduleName, fieldName, functionIndex, typeIndex))
    }

    public override fun visitGlobal(importIndex: UInt, moduleName: String, fieldName: String, globalIndex: UInt, type: WasmType, mutable: Boolean) {
        val globalType = GlobalTypeNode(type, mutable)

        imports.add(GlobalImportNode(importIndex, moduleName, fieldName, globalIndex, globalType))
    }

    public override fun visitTable(importIndex: UInt, moduleName: String, fieldName: String, tableIndex: UInt, elementType: WasmType, limits: ResizableLimits) {
        val tableType = TableTypeNode(tableIndex, elementType, limits)

        imports.add(TableImportNode(importIndex, moduleName, fieldName, tableIndex, tableType))
    }

    public override fun visitMemory(importIndex: UInt, moduleName: String, fieldName: String, memoryIndex: UInt, limits: ResizableLimits) {
        val memoryType = MemoryTypeNode(memoryIndex, limits)

        imports.add(MemoryImportNode(importIndex, moduleName, fieldName, memoryIndex, memoryType))
    }

    public override fun visitException(importIndex: UInt, moduleName: String, fieldName: String, exceptionIndex: UInt, exceptionTypes: Array<WasmType>) {
        val exceptionType = ExceptionTypeNode(exceptionIndex, exceptionTypes)

        imports.add(ExceptionImportNode(importIndex, moduleName, fieldName, exceptionIndex, exceptionType))
    }

    public override fun visitEnd() {
        // empty
    }
}
