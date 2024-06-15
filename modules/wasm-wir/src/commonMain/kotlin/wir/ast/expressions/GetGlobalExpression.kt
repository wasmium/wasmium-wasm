package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirGlobal
import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class GetGlobalExpression(
    public val global: WirGlobal,
) : WirExpression(WirNodeKind.GET_GLOBAL) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitGetGlobal(this)
    }
}
