package org.wasmium.wasm.binary.tree.sections

public class ModuleNameNode(
    public var name: String
) : NameNode(NameNodeKind.MODULE)
