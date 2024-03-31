package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public open class TypeSectionAdapter(protected val delegate: TypeSectionVisitor? = null) : TypeSectionVisitor {
    public override fun visitType(parameters: List<WasmType>, results: List<WasmType>) {
        delegate?.visitType(parameters, results)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
