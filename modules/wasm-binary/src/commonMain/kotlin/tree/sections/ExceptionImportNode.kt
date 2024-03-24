package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class ExceptionImportNode(
    public override val importIndex: UInt,
    public override val module: String,
    public override val name: String,
    public val exceptionIndex: UInt,
    public val exceptionType: ExceptionTypeNode,
) : ImportNode(importIndex, module, name, ExternalKind.EXCEPTION) {
    override fun accept(importSectionVisitor: ImportSectionVisitor) {
        importSectionVisitor.visitException(importIndex, module, name, exceptionIndex, exceptionType.exceptionTypes)
    }
}
