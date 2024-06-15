package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirIntType

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class IntBinaryExpression(
    public val type: WirIntType,
    public val operation: IntBinaryOperation,
    public val first: WirExpression,
    public val second: WirExpression,
) : WirExpression(WirNodeKind.FLOAT_BINARY) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitIntBinary(this)
    }
}
