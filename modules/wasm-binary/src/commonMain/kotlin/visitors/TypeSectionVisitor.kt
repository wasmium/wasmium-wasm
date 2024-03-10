package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public interface TypeSectionVisitor {
    public fun visitType(typeIndex: UInt, parameters: Array<WasmType>, results: Array<WasmType>)

    public fun visitEnd()
}
