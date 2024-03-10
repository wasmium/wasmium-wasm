package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType


public interface ImportSectionVisitor {
    public fun visitFunction(importIndex: UInt, moduleName: String, fieldName: String, functionIndex: UInt, typeIndex: UInt)

    public fun visitGlobal(importIndex: UInt, moduleName: String, fieldName: String, globalIndex: UInt, type: WasmType, mutable: Boolean)

    public fun visitTable(importIndex: UInt, moduleName: String, fieldName: String, tableIndex: UInt, elementType: WasmType, limits: ResizableLimits)

    public fun visitMemory(importIndex: UInt, moduleName: String, fieldName: String, memoryIndex: UInt, limits: ResizableLimits)

    public fun visitException(importIndex: UInt, moduleName: String, fieldName: String, exceptionIndex: UInt, exceptionTypes: Array<WasmType>)

    public fun visitEnd()
}
