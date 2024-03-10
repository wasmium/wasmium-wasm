package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind

public class ExportTypeNode {
    public var exportIndex: UInt? = null
    public var name: String? = null
    public var kind: ExternalKind? = null

    /** Index into the corresponding index space.  */
    public var index: UInt? = null
}
