package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class TagImportNode(
    public override val module: String,
    public override val name: String,
    public val tagType: TagType,
) : ImportNode(module, name, ExternalKind.TAG) {
    override fun accept(importSectionVisitor: ImportSectionVisitor) {
        importSectionVisitor.visitTag(module, name, tagType)
    }
}
