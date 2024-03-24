package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class MemoryImportNode(
    public override val importIndex: UInt,
    public override val module: String,
    public override val name: String,
    public val memoryIndex: UInt,
    public val memoryType: MemoryTypeNode,
) : ImportNode(importIndex, module, name, ExternalKind.MEMORY) {
    override fun accept(importSectionVisitor: ImportSectionVisitor) {
        importSectionVisitor.visitMemory(importIndex, module, name, memoryIndex, memoryType.limits)
    }
}
