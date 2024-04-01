package org.wasmium.wasm.binary.visitors

public interface FunctionSectionVisitor {

    public fun visitFunction(typeIndex: UInt)

    public fun visitEnd()
}
