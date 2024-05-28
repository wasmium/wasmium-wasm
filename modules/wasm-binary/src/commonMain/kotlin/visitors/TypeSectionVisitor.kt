package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.FunctionType

public interface TypeSectionVisitor {

    public fun visitType(functionType: FunctionType)

    public fun visitEnd()
}
