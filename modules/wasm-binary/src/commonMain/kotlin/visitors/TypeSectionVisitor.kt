package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public interface TypeSectionVisitor {
    public fun visitType(parameters: List<WasmType>, results: List<WasmType>)

    public fun visitEnd()
}
