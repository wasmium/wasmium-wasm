package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.GlobalType.Mutability

public interface GlobalSectionVisitor {

    public fun visitGlobalVariable(type: WasmType, mutability: Mutability): ExpressionVisitor

    public fun visitEnd()
}
