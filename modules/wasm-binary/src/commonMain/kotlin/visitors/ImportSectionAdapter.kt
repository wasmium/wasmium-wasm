package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType

public class ImportSectionAdapter(protected val delegate: ImportSectionVisitor? = null) : ImportSectionVisitor {
    public override fun visitFunction(importIndex: UInt, moduleName: String, fieldName: String, functionIndex: UInt, typeIndex: UInt) {
        delegate?.visitFunction(importIndex, moduleName, fieldName, functionIndex, typeIndex)
    }

    public override fun visitGlobal(importIndex: UInt, moduleName: String, fieldName: String, globalIndex: UInt, type: WasmType, mutable: Boolean) {
        delegate?.visitGlobal(importIndex, moduleName, fieldName, globalIndex, type, mutable)
    }

    public override fun visitTable(importIndex: UInt, moduleName: String, fieldName: String, tableIndex: UInt, elementType: WasmType, limits: ResizableLimits) {
        delegate?.visitTable(importIndex, moduleName, fieldName, tableIndex, elementType, limits)
    }

    public override fun visitMemory(importIndex: UInt, moduleName: String, fieldName: String, memoryIndex: UInt, limits: ResizableLimits) {
        delegate?.visitMemory(importIndex, moduleName, fieldName, memoryIndex, limits)
    }

    public override fun visitException(importIndex: UInt, moduleName: String, fieldName: String, exceptionIndex: UInt, exceptionTypes: Array<WasmType>) {
        delegate?.visitException(importIndex, moduleName, fieldName, exceptionIndex, exceptionTypes)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
