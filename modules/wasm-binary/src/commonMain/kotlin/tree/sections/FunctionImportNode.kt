package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.FunctionType
import org.wasmium.wasm.binary.visitor.ImportSectionVisitor

public class FunctionImportNode(
    public override val module: String,
    public override val name: String,
    public val functionType: FunctionType,
) : ImportNode(module, name, ExternalKind.FUNCTION) {
    public override fun accept(importSectionVisitor: ImportSectionVisitor) {
        importSectionVisitor.visitFunction(module, name, functionType)
    }
}
