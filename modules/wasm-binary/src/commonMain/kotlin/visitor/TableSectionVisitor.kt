package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.TableType

public interface TableSectionVisitor {

    public fun visitTable(tableType: TableType)

    public fun visitEnd()
}
