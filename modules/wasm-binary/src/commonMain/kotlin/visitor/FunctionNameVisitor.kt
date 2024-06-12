package org.wasmium.wasm.binary.visitor

public interface FunctionNameVisitor {

    public fun visitFunctionName(functionName: String)

    public fun visitEnd()
}
