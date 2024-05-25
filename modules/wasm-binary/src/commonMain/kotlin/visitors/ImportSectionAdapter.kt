package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.GlobalType.Mutability
import org.wasmium.wasm.binary.tree.MemoryLimits
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.tree.TypeIndex
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.sections.MemoryType

public open class ImportSectionAdapter(protected val delegate: ImportSectionVisitor? = null) : ImportSectionVisitor {

    override fun visitFunction(moduleName: String, fieldName: String, typeIndex: TypeIndex): Unit = delegate?.visitFunction(moduleName, fieldName, typeIndex) ?: Unit

    override fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutability: Mutability): Unit =
        delegate?.visitGlobal(moduleName, fieldName, type, mutability) ?: Unit

    override fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: MemoryLimits): Unit =
        delegate?.visitTable(moduleName, fieldName, elementType, limits) ?: Unit

    override fun visitMemory(moduleName: String, fieldName: String, memoryType: MemoryType): Unit = delegate?.visitMemory(moduleName, fieldName, memoryType) ?: Unit

    override fun visitTag(moduleName: String, fieldName: String, tagType: TagType): Unit = delegate?.visitTag(moduleName, fieldName, tagType) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
