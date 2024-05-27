package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.sections.TableType

public interface TableSectionVisitor {

    public fun visitTable(tableType: TableType)

    public fun visitEnd()
}
