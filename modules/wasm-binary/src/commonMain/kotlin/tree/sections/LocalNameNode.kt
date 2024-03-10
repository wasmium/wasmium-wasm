package org.wasmium.wasm.binary.tree.sections

public class LocalNameNode : NameNode {
    public var localIndex: UInt? = null
    public var name: String? = null
    public var functionIndex: UInt? = null

    public override val nameKind: NameNodeKind
        get() = NameNodeKind.LOCAL
}
