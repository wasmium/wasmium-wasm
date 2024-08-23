package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.LinkingKind
import org.wasmium.wasm.binary.tree.LinkingSymbolType

public class SymbolTableLinkingNode : LinkingNode(LinkingKind.SYMBOL_TABLE) {
    public var name: String? = null
    public var flags: UInt? = null
    public var index: UInt? = null
    public var symbolType: LinkingSymbolType? = null
}
