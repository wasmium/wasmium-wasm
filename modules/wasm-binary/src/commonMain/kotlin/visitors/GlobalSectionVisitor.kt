package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public interface GlobalSectionVisitor {

    public fun visitGlobalVariable(type: WasmType, mutable: Boolean): ExpressionVisitor

    public fun visitEnd()
}

