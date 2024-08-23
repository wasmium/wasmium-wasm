package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirGlobal
import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class SetGlobalExpression(
    public val global: WirGlobal,
    public val value: WirExpression,
) : WirExpression(WirNodeKind.SET_GLOBAL) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitSetGlobal(this)
    }
}
