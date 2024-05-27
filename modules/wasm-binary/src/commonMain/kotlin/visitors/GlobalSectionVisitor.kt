package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.GlobalType

public interface GlobalSectionVisitor {

    public fun visitGlobalVariable(globalType: GlobalType): ExpressionVisitor

    public fun visitEnd()
}
