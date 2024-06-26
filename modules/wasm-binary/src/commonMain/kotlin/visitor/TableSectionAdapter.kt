package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.TableType

public open class TableSectionAdapter(protected val delegate: TableSectionVisitor? = null) : TableSectionVisitor {

    override fun visitTable(tableType: TableType): Unit = delegate?.visitTable(tableType) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
