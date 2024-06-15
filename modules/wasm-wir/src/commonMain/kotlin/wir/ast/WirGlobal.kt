package org.wasmium.wir.ast

import org.wasmium.wir.ast.expressions.WirExpression
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class WirGlobal(
    public val signature: WirGlobalSignature,
    public val initializer: WirExpression,
) : WirObject(WirNodeKind.GLOBAL) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitGlobal(this)
    }
}
