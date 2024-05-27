package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.MemoryType
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class MemoryImportNode(
    public override val module: String,
    public override val name: String,
    public val memoryType: MemoryType,
) : ImportNode(module, name, ExternalKind.MEMORY) {
    override fun accept(importSectionVisitor: ImportSectionVisitor) {
        importSectionVisitor.visitMemory(module, name, memoryType)
    }
}
