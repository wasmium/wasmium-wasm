package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.GlobalType
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class GlobalImportNode(
    public override val module: String,
    public override val name: String,
    public val globalType: GlobalType,
) : ImportNode(module, name, ExternalKind.GLOBAL) {
    override fun accept(importSectionVisitor: ImportSectionVisitor) {
        importSectionVisitor.visitGlobal(module, name, globalType.contentType, globalType.mutability)
    }
}
