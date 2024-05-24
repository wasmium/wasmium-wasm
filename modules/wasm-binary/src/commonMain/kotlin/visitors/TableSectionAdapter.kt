package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.MemoryLimits
import org.wasmium.wasm.binary.tree.WasmType

public open class TableSectionAdapter(protected val delegate: TableSectionVisitor? = null) : TableSectionVisitor {

    override fun visitTable(elementType: WasmType, limits: MemoryLimits): Unit = delegate?.visitTable(elementType, limits) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
