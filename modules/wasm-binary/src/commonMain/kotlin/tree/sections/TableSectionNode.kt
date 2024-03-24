package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.TableSectionVisitor

public class TableSectionNode : SectionNode(SectionKind.TABLE), TableSectionVisitor {
    public val tables: MutableList<TableTypeNode> = mutableListOf()

    public fun accept(tableSectionVisitor: TableSectionVisitor) {
        for (tableType in tables) {
            tableSectionVisitor.visitTable(tableType.tableIndex, tableType.elementType, tableType.limits)
        }

        tableSectionVisitor.visitEnd()
    }

    public override fun visitTable(tableIndex: UInt, elementType: WasmType, limits: ResizableLimits) {
        tables.add(TableTypeNode(tableIndex, elementType, limits))
    }

    public override fun visitEnd() {
        // empty
    }
}
