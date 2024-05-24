package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.GlobalType.Mutability

public open class ImportSectionAdapter(protected val delegate: ImportSectionVisitor? = null) : ImportSectionVisitor {

    override fun visitFunction(moduleName: String, fieldName: String, typeIndex: UInt): Unit = delegate?.visitFunction(moduleName, fieldName, typeIndex) ?: Unit

    override fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutability: Mutability): Unit =
        delegate?.visitGlobal(moduleName, fieldName, type, mutability) ?: Unit

    override fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: ResizableLimits): Unit =
        delegate?.visitTable(moduleName, fieldName, elementType, limits) ?: Unit

    override fun visitMemory(moduleName: String, fieldName: String, limits: ResizableLimits): Unit = delegate?.visitMemory(moduleName, fieldName, limits) ?: Unit

    override fun visitTag(moduleName: String, fieldName: String, tagType: TagType): Unit = delegate?.visitTag(moduleName, fieldName, tagType) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
