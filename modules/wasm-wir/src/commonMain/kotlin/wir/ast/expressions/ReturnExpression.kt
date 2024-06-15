package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class ReturnExpression(public val value: WirExpression) : WirExpression(WirNodeKind.RETURN) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitReturn(this)
    }
}
