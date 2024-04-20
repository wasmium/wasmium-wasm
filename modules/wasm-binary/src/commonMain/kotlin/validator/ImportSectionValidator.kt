package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.sections.ExceptionType
import org.wasmium.wasm.binary.tree.sections.GlobalType
import org.wasmium.wasm.binary.tree.sections.MemoryType
import org.wasmium.wasm.binary.tree.sections.TableType
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class ImportSectionValidator(private val delegate: ImportSectionVisitor? = null, private val context: ValidatorContext) : ImportSectionVisitor {
    override fun visitFunction(moduleName: String, fieldName: String, typeIndex: UInt) {
        context.functions.add(context.resultType(typeIndex))

        delegate?.visitFunction(moduleName, fieldName, typeIndex)
    }

    override fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutable: Boolean) {
        context.globals.add(GlobalType(type, mutable))

        delegate?.visitGlobal(moduleName, fieldName, type, mutable)
    }

    override fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: ResizableLimits) {
        context.tables.add(TableType(elementType, limits))

        delegate?.visitTable(moduleName, fieldName, elementType, limits)
    }

    override fun visitMemory(moduleName: String, fieldName: String, limits: ResizableLimits) {
        context.memories.add(MemoryType(limits))

        delegate?.visitMemory(moduleName, fieldName, limits)
    }

    override fun visitException(moduleName: String, fieldName: String, exceptionTypes: List<WasmType>) {
        context.exceptions.add(ExceptionType(exceptionTypes))

        delegate?.visitException(moduleName, fieldName, exceptionTypes)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
