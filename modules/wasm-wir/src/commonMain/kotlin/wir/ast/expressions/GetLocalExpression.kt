package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirLocal
import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class GetLocalExpression(
    public val local: WirLocal,
) : WirExpression(WirNodeKind.GET_LOCAL) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitGetLocal(this)
    }
}
