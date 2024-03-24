package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class FunctionImportNode(
    public override val importIndex: UInt,
    public override val module: String,
    public override val name: String,
    public val functionIndex: UInt,
    public val typeIndex: UInt,
) : ImportNode(importIndex, module, name, ExternalKind.FUNCTION) {
    public override fun accept(importSectionVisitor: ImportSectionVisitor) {
        importSectionVisitor.visitFunction(importIndex, module, name, functionIndex, typeIndex)
    }
}
