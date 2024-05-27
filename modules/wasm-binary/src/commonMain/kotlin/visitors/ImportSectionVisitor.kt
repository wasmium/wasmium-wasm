package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.GlobalType
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.tree.TypeIndex
import org.wasmium.wasm.binary.tree.MemoryType
import org.wasmium.wasm.binary.tree.TableType

public interface ImportSectionVisitor {

    public fun visitFunction(moduleName: String, fieldName: String, typeIndex: TypeIndex)

    public fun visitGlobal(moduleName: String, fieldName: String, globalType: GlobalType)

    public fun visitTable(moduleName: String, fieldName: String, tableType: TableType)

    public fun visitMemory(moduleName: String, fieldName: String, memoryType: MemoryType)

    public fun visitTag(moduleName: String, fieldName: String, tagType: TagType)

    public fun visitEnd()
}
