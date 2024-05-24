package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.MemoryLimits
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.GlobalType
import org.wasmium.wasm.binary.tree.GlobalType.Mutability
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

    override fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutability: Mutability) {
        context.checkGlobalType(type, mutability)

        context.globals.add(GlobalType(type, mutability))

        delegate?.visitGlobal(moduleName, fieldName, type, mutability)
    }

    override fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: MemoryLimits) {
        context.checkTableType(elementType, limits)

        context.tables.add(TableType(elementType, limits))

        delegate?.visitTable(moduleName, fieldName, elementType, limits)
    }

    override fun visitMemory(moduleName: String, fieldName: String, limits: MemoryLimits) {
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
