package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirIntType

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class IntUnaryExpression(
    public val type: WirIntType,
    public val operation: IntUnaryOperation,
    public val operand: WirExpression,
) : WirExpression(WirNodeKind.FLOAT_UNARY) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitIntUnary(this)
    }
}
