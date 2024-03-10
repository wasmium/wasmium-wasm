package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public interface ExceptionSectionVisitor {
    public fun visitExceptionType(exceptionIndex: UInt, types: Array<WasmType>)

    public fun visitEnd()
}
