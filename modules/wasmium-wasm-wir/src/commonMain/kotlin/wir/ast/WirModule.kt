package org.wasmium.wir.ast

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class WirModule(
    protected val functions: List<WirFunction>,
    protected val globals: List<WirGlobal>,
    protected val memories: List<WirMemory>,
    protected val tables: List<WirTable>,
    public var name: String,
) : WirNode(WirNodeKind.MODULE) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitModule(this)
    }
}
