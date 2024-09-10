package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirFloatType

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class FloatUnaryExpression(
    public val type: WirFloatType,
    public val operation: FloatUnaryOperation,
    public val operand: WirExpression,
) : WirExpression(WirNodeKind.FLOAT_UNARY) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitFloatUnary(this)
    }
}
