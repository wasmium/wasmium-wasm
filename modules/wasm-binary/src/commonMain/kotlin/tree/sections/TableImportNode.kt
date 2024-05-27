package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class TableImportNode(
    public override val module: String,
    public override val name: String,
    public val tableType: TableType,
) : ImportNode(module, name, ExternalKind.TABLE) {
    override fun accept(importSectionVisitor: ImportSectionVisitor) {
        importSectionVisitor.visitTable(module, name, tableType)
    }
}
