package org.wasmium.wasm.binary.visitors

public interface FunctionSectionVisitor {
    public fun visitFunction(functionIndex: UInt, typeIndex: UInt)

    public fun visitEnd()
}
