package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType

public open class ImportSectionAdapter(protected val delegate: ImportSectionVisitor? = null) : ImportSectionVisitor {

    public override fun visitFunction(moduleName: String, fieldName: String, typeIndex: UInt): Unit = delegate?.visitFunction(moduleName, fieldName, typeIndex) ?: Unit

    public override fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutable: Boolean): Unit =
        delegate?.visitGlobal(moduleName, fieldName, type, mutable) ?: Unit

    public override fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: ResizableLimits): Unit =
        delegate?.visitTable(moduleName, fieldName, elementType, limits) ?: Unit

    public override fun visitMemory(moduleName: String, fieldName: String, limits: ResizableLimits): Unit = delegate?.visitMemory(moduleName, fieldName, limits) ?: Unit

    public override fun visitException(moduleName: String, fieldName: String, exceptionTypes: List<WasmType>): Unit =
        delegate?.visitException(moduleName, fieldName, exceptionTypes) ?: Unit

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
