package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind

public class GlobalImportNode : ImportNode() {
    public var globalIndex: UInt? = null
    public var globalType: GlobalTypeNode? = null

    public override val externalKind: ExternalKind
        get() = ExternalKind.GLOBAL
}
