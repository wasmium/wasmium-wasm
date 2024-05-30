package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.LocalVariable

public interface CodeSectionVisitor {

    public fun visitCode(locals: List<LocalVariable>): ExpressionVisitor

    public fun visitEnd()
}
