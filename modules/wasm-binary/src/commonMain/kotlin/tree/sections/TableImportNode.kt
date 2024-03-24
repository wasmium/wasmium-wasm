package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class TableImportNode(
    public override val importIndex: UInt,
    public override val module: String,
    public override val name: String,
    public val tableIndex: UInt,
    public val tableType: TableTypeNode,
) : ImportNode(importIndex, module, name, ExternalKind.TABLE) {
    override fun accept(importSectionVisitor: ImportSectionVisitor) {
        importSectionVisitor.visitTable(importIndex, module, name, tableIndex, tableType.elementType, tableType.limits)
    }
}
