package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public class ExceptionSectionAdapter(protected val delegate: ExceptionSectionVisitor? = null) : ExceptionSectionVisitor {
    public override fun visitExceptionType(exceptionIndex: UInt, types: Array<WasmType>) {
        delegate?.visitExceptionType(exceptionIndex, types)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
