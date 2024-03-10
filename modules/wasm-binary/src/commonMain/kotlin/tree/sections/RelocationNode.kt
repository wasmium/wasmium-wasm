package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.RelocationKind

public class RelocationNode {
    public var relocationKind: RelocationKind? = null
    public var offset: UInt? = null
    public var index: UInt? = null
    public var addend: Int? = null
}
