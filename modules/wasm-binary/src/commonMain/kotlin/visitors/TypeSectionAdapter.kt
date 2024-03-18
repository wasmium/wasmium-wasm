package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.WasmType

public open class TypeSectionAdapter(protected val delegate: TypeSectionVisitor? = null) : TypeSectionVisitor {
    public override fun visitType(typeIndex: UInt, parameters: Array<WasmType>, results: Array<WasmType>) {
        delegate?.visitType(typeIndex, parameters, results)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
