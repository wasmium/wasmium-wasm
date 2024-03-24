package org.wasmium.wasm.binary.tree.sections

public class LocalNameNode(
    public var functionIndex: UInt,
    public var localIndex: UInt,
    public var name: String,
) : NameNode(NameNodeKind.LOCAL)
