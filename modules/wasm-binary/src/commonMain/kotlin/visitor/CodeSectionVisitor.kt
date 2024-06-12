package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.LocalVariable

public interface CodeSectionVisitor {

    public fun visitCode(locals: List<LocalVariable>): ExpressionVisitor

    public fun visitEnd()
}
