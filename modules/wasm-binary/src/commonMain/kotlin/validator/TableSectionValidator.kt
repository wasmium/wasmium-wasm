package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.TableType
import org.wasmium.wasm.binary.visitor.TableSectionVisitor

public class TableSectionValidator(private val delegate: TableSectionVisitor? = null, private val context: ValidatorContext) : TableSectionVisitor {
    override fun visitTable(tableType: TableType) {
        context.checkTableType(tableType)

        context.tables.add(tableType)

        delegate?.visitTable(tableType)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
