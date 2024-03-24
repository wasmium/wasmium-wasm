package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.RelocationKind

public class RelocationNode(
    public var relocationKind: RelocationKind,
    public var offset: UInt,
    public var index: UInt,
    public var addend: Int? = null,
)
