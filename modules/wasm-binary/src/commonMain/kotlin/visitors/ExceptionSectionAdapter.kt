package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public open class ExceptionSectionAdapter(protected val delegate: ExceptionSectionVisitor? = null) : ExceptionSectionVisitor {

    public override fun visitExceptionType(types: List<WasmType>): Unit = delegate?.visitExceptionType(types) ?: Unit

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
