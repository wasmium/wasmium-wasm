package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public open class ExceptionSectionAdapter(protected val delegate: ExceptionSectionVisitor? = null) : ExceptionSectionVisitor {
    public override fun visitExceptionType(types: List<WasmType>) {
        delegate?.visitExceptionType(types)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
