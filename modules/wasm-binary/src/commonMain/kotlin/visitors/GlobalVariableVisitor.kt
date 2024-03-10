package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public interface GlobalVariableVisitor {
    public fun visitInitializerExpression(): InitializerExpressionVisitor

    public fun visitGlobalVariable(type: WasmType, mutable: Boolean)

    public fun visitEnd()
}
