package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.FunctionType

public interface FunctionSectionVisitor {

    public fun visitFunction(functionType: FunctionType)

    public fun visitEnd()
}
