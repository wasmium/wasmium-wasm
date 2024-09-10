package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.IndexName

public class LocalNameNode(
    public var functionIndex: UInt,
    public var names: List<IndexName>,
) : NameNode(NameNodeKind.LOCAL)
