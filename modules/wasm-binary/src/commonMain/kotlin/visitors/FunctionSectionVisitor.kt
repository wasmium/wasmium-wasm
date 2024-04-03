package org.wasmium.wasm.binary.visitors

public interface FunctionSectionVisitor {

    public fun visitFunction(signatureIndex: UInt)

    public fun visitEnd()
}
