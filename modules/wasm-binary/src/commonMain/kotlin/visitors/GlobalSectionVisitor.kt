package org.wasmium.wasm.binary.visitors

public interface GlobalSectionVisitor {
    public fun visitGlobalVariable(globalIndex: UInt): GlobalVariableVisitor

    public fun visitEnd()
}

