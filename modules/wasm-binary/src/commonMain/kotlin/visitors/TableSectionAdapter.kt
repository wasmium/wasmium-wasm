package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType

public open class TableSectionAdapter(protected val delegate: TableSectionVisitor? = null) : TableSectionVisitor {
    public override fun visitTable(tableIndex: UInt, elementType: WasmType, limits: ResizableLimits) {
        delegate?.visitTable(tableIndex, elementType, limits)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
