package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType

public interface ImportSectionVisitor {
    public fun visitFunction(moduleName: String, fieldName: String, typeIndex: UInt)

    public fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutable: Boolean)

    public fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: ResizableLimits)

    public fun visitMemory(moduleName: String, fieldName: String, limits: ResizableLimits)

    public fun visitException(moduleName: String, fieldName: String, exceptionTypes: List<WasmType>)

    public fun visitEnd()
}
