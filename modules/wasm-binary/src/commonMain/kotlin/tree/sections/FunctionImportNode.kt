package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class FunctionImportNode(
    public override val module: String,
    public override val name: String,
    public val typeIndex: UInt,
) : ImportNode(module, name, ExternalKind.FUNCTION) {
    public override fun accept(importSectionVisitor: ImportSectionVisitor) {
        importSectionVisitor.visitFunction(module, name, typeIndex)
    }
}
