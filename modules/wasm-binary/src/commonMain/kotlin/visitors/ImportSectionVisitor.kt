package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.MemoryLimits
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.GlobalType.Mutability
import org.wasmium.wasm.binary.tree.TypeIndex
import org.wasmium.wasm.binary.tree.sections.MemoryType

public interface ImportSectionVisitor {

    public fun visitFunction(moduleName: String, fieldName: String, typeIndex: TypeIndex)

    public fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutability: Mutability)

    public fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: MemoryLimits)

    public fun visitMemory(moduleName: String, fieldName: String, memoryType: MemoryType)

    public fun visitTag(moduleName: String, fieldName: String, tagType: TagType)

    public fun visitEnd()
}
