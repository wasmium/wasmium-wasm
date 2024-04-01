package org.wasmium.wasm.binary.visitors

public interface FunctionNameVisitor {

    public fun visitFunctionName(functionName: String)

    public fun visitEnd()
}
