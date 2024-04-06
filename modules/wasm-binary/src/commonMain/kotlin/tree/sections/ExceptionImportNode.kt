package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class ExceptionImportNode(
    public override val module: String,
    public override val name: String,
    public val exceptionType: ExceptionType,
) : ImportNode(module, name, ExternalKind.EXCEPTION) {
    override fun accept(importSectionVisitor: ImportSectionVisitor) {
        importSectionVisitor.visitException(module, name, exceptionType.exceptionTypes)
    }
}
