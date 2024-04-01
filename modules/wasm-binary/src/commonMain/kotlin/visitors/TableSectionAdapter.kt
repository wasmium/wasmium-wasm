package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType

public open class TableSectionAdapter(protected val delegate: TableSectionVisitor? = null) : TableSectionVisitor {

    public override fun visitTable(elementType: WasmType, limits: ResizableLimits): Unit = delegate?.visitTable(elementType, limits) ?: Unit

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
