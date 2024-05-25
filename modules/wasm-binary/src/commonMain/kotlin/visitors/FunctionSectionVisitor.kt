package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.TypeIndex

public interface FunctionSectionVisitor {

    public fun visitFunction(typeIndex: TypeIndex)

    public fun visitEnd()
}
