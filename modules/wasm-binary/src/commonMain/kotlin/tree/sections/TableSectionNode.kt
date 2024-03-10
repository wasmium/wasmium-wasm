package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.visitors.TableSectionVisitor

public class TableSectionNode : SectionNode(SectionKind.TABLE), TableSectionVisitor {
    public val tables: MutableList<TableTypeNode> = mutableListOf<TableTypeNode>()

    public fun accept(tableSectionVisitor: TableSectionVisitor) {
        for (tableType in tables) {
            tableSectionVisitor.visitTable(tableType.tableIndex!!, tableType.elementType!!, tableType.limits!!)
        }
    }

    public override fun visitTable(tableIndex: UInt, elementType: WasmType, limits: ResizableLimits) {
        val tableType: TableTypeNode = TableTypeNode()
        tableType.tableIndex = tableIndex
        tableType.elementType = elementType
        tableType.limits = limits

        tables.add(tableType)
    }

    public override fun visitEnd() {
        // empty
    }
}
