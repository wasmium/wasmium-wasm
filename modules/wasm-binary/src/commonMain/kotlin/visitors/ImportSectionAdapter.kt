package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType

public open class ImportSectionAdapter(protected val delegate: ImportSectionVisitor? = null) : ImportSectionVisitor {

    override fun visitFunction(moduleName: String, fieldName: String, typeIndex: UInt): Unit = delegate?.visitFunction(moduleName, fieldName, typeIndex) ?: Unit

    override fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutable: Boolean): Unit =
        delegate?.visitGlobal(moduleName, fieldName, type, mutable) ?: Unit

    override fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: ResizableLimits): Unit =
        delegate?.visitTable(moduleName, fieldName, elementType, limits) ?: Unit

    override fun visitMemory(moduleName: String, fieldName: String, limits: ResizableLimits): Unit = delegate?.visitMemory(moduleName, fieldName, limits) ?: Unit

    override fun visitException(moduleName: String, fieldName: String, exceptionTypes: List<WasmType>): Unit =
        delegate?.visitException(moduleName, fieldName, exceptionTypes) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
