package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class DropExpression(
    public val operand: WirExpression,
) : WirExpression(WirNodeKind.DROP) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitDrop(this)
    }
}
