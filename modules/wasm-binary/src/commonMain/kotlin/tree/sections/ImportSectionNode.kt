package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.GlobalType
import org.wasmium.wasm.binary.tree.MemoryLimits
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.GlobalType.*
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class ImportSectionNode : SectionNode(SectionKind.IMPORT), ImportSectionVisitor {
    public val imports: MutableList<ImportNode> = mutableListOf()

    public fun accept(importSectionVisitor: ImportSectionVisitor) {
        for (import in imports) {
            import.accept(importSectionVisitor)
        }

        importSectionVisitor.visitEnd()
    }

    public override fun visitFunction(moduleName: String, fieldName: String, typeIndex: UInt) {
        imports.add(FunctionImportNode(moduleName, fieldName, typeIndex))
    }

    public override fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutability: Mutability) {
        val globalType = GlobalType(type, mutability)

        imports.add(GlobalImportNode(moduleName, fieldName, globalType))
    }

    public override fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: MemoryLimits) {
        val tableType = TableType(elementType, limits)

        imports.add(TableImportNode(moduleName, fieldName, tableType))
    }

    public override fun visitMemory(moduleName: String, fieldName: String, memoryType: MemoryType) {
        imports.add(MemoryImportNode(moduleName, fieldName, memoryType))
    }

    public override fun visitTag(moduleName: String, fieldName: String, tagType: TagType) {
        imports.add(TagImportNode(moduleName, fieldName, tagType))
    }

    public override fun visitEnd() {
        // empty
    }
}
