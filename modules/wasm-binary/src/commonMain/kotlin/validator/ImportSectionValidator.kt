package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.sections.GlobalType
import org.wasmium.wasm.binary.tree.sections.MemoryType
import org.wasmium.wasm.binary.tree.sections.TableType
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class ImportSectionValidator(private val delegate: ImportSectionVisitor? = null, private val context: ValidatorContext) : ImportSectionVisitor {
    override fun visitFunction(moduleName: String, fieldName: String, typeIndex: UInt) {
        val functionType = context.types[typeIndex.toInt()]

        context.functions.add(functionType)

        context.numberOfImportFunctions++

        delegate?.visitFunction(moduleName, fieldName, typeIndex)
    }

    override fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutable: Boolean) {
        context.checkGlobalType(type, mutable)

        context.globals.add(GlobalType(type, mutable))

        delegate?.visitGlobal(moduleName, fieldName, type, mutable)
    }

    override fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: ResizableLimits) {
        context.checkTableType(elementType, limits)

        context.tables.add(TableType(elementType, limits))

        delegate?.visitTable(moduleName, fieldName, elementType, limits)
    }

    override fun visitMemory(moduleName: String, fieldName: String, limits: ResizableLimits) {
        context.checkMemoryType(limits)

        context.memories.add(MemoryType(limits))

        delegate?.visitMemory(moduleName, fieldName, limits)
    }

    override fun visitTag(moduleName: String, fieldName: String, tagType: TagType) {
        context.tags.add(tagType)

        delegate?.visitTag(moduleName, fieldName, tagType)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
