package org.wasmium.wasm.binary.visitors

public interface CodeSectionVisitor {
    public fun visitFunctionBody(functionIndex: UInt): FunctionBodyVisitor

    public fun visitEnd()
}
