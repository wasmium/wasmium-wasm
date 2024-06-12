package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.TableType
import org.wasmium.wasm.binary.visitor.TableSectionVisitor

public class TableSectionNode : SectionNode(SectionKind.TABLE), TableSectionVisitor {
    public val tables: MutableList<TableType> = mutableListOf()

    public fun accept(tableSectionVisitor: TableSectionVisitor) {
        for (tableType in tables) {
            tableSectionVisitor.visitTable(tableType)
        }

        tableSectionVisitor.visitEnd()
    }

    public override fun visitTable(tableType: TableType) {
        tables.add(tableType)
    }

    public override fun visitEnd() {
        // empty
    }
}
