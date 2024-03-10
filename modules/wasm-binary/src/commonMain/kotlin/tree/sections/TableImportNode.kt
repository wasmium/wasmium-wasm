package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind

public class TableImportNode : ImportNode() {
    public var tableIndex: UInt? = null
    public var tableType: TableTypeNode? = null

    public override val externalKind: ExternalKind
        get() = ExternalKind.TABLE
}
