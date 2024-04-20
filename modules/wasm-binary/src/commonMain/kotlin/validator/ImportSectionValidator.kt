package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class ImportSectionValidator(private val delegate: ImportSectionVisitor, private val context: ValidatorContext) : ImportSectionVisitor {
    override fun visitFunction(moduleName: String, fieldName: String, typeIndex: UInt) {
        delegate.visitFunction(moduleName, fieldName, typeIndex)
    }

    override fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutable: Boolean) {
        delegate.visitGlobal(moduleName, fieldName, type, mutable)
    }

    override fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: ResizableLimits) {
        delegate.visitTable(moduleName, fieldName, elementType, limits)
    }

    override fun visitMemory(moduleName: String, fieldName: String, limits: ResizableLimits) {
        delegate.visitMemory(moduleName, fieldName, limits)
    }

    override fun visitException(moduleName: String, fieldName: String, exceptionTypes: List<WasmType>) {
        delegate.visitException(moduleName, fieldName, exceptionTypes)
    }

    override fun visitEnd() {
        delegate.visitEnd()
    }
}
