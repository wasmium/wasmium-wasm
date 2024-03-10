package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind

public class MemoryImportNode : ImportNode() {
    public var memoryIndex: UInt? = null
    public var memoryType: MemoryTypeNode? = null

    public override val externalKind: ExternalKind
        get() = ExternalKind.MEMORY

}
