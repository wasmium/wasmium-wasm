package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class SelectExpression(
    public val condition: WirExpression,
    public val ifTrue: WirExpression,
    public val ifFalse: WirExpression,
) : WirExpression(WirNodeKind.SELECT) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitSelect(this)
    }
}
