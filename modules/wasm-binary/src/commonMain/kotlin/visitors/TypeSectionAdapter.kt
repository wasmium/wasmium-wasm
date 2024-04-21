package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public open class TypeSectionAdapter(protected val delegate: TypeSectionVisitor? = null) : TypeSectionVisitor {

    override fun visitType(parameters: List<WasmType>, results: List<WasmType>): Unit = delegate?.visitType(parameters, results) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
