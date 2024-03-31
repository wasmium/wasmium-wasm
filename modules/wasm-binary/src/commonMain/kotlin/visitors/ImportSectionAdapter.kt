package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType

public open class ImportSectionAdapter(protected val delegate: ImportSectionVisitor? = null) : ImportSectionVisitor {
    public override fun visitFunction(moduleName: String, fieldName: String, typeIndex: UInt) {
        delegate?.visitFunction(moduleName, fieldName, typeIndex)
    }

    public override fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutable: Boolean) {
        delegate?.visitGlobal(moduleName, fieldName, type, mutable)
    }

    public override fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: ResizableLimits) {
        delegate?.visitTable(moduleName, fieldName, elementType, limits)
    }

    public override fun visitMemory(moduleName: String, fieldName: String, limits: ResizableLimits) {
        delegate?.visitMemory(moduleName, fieldName, limits)
    }

    public override fun visitException(moduleName: String, fieldName: String, exceptionTypes: List<WasmType>) {
        delegate?.visitException(moduleName, fieldName, exceptionTypes)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
