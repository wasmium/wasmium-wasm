package org.wasmium.wasm.binary.visitors

public interface StartSectionVisitor {
    public fun visitStartFunctionIndex(functionIndex: UInt)

    public fun visitEnd()
}
