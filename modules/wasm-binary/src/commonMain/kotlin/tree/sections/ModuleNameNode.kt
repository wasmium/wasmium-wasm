package org.wasmium.wasm.binary.tree.sections

public class ModuleNameNode : NameNode {
    public var name: String? = null

    public override val nameKind: NameNodeKind
        get() = NameNodeKind.MODULE
}
