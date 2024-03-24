package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class GlobalImportNode(
    public override val importIndex: UInt,
    public override val module: String,
    public override val name: String,
    public val globalIndex: UInt,
    public val globalType: GlobalTypeNode,
) : ImportNode(importIndex, module, name, ExternalKind.GLOBAL) {
    override fun accept(importSectionVisitor: ImportSectionVisitor) {
        importSectionVisitor.visitGlobal(importIndex, module, name, globalIndex, globalType.contentType, globalType.isMutable)
    }
}
