package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.FunctionType
import org.wasmium.wasm.binary.tree.GlobalType
import org.wasmium.wasm.binary.tree.MemoryType
import org.wasmium.wasm.binary.tree.TableType
import org.wasmium.wasm.binary.tree.TagType

public open class ImportSectionAdapter(protected val delegate: ImportSectionVisitor? = null) : ImportSectionVisitor {

    override fun visitFunction(moduleName: String, fieldName: String, functionType: FunctionType): Unit = delegate?.visitFunction(moduleName, fieldName, functionType) ?: Unit

    override fun visitGlobal(moduleName: String, fieldName: String, globalType: GlobalType): Unit = delegate?.visitGlobal(moduleName, fieldName, globalType) ?: Unit

    override fun visitTable(moduleName: String, fieldName: String, tableType: TableType): Unit = delegate?.visitTable(moduleName, fieldName, tableType) ?: Unit

    override fun visitMemory(moduleName: String, fieldName: String, memoryType: MemoryType): Unit = delegate?.visitMemory(moduleName, fieldName, memoryType) ?: Unit

    override fun visitTag(moduleName: String, fieldName: String, tagType: TagType): Unit = delegate?.visitTag(moduleName, fieldName, tagType) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
