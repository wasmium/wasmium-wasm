package org.wasmium.wir.ast

import org.wasmium.wir.ast.type.WirValueType
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class WirLocal(
    public val function: WirFunction,
    public val name: String,
    public val type: WirValueType,
) : WirNode(WirNodeKind.LOCAL) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitLocal(this)
    }
}
