package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirLocal
import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class SetLocalExpression(
    public val local: WirLocal,
    public val value: WirExpression,
) : WirExpression(WirNodeKind.SET_LOCAL) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitSetLocal(this)
    }
}
