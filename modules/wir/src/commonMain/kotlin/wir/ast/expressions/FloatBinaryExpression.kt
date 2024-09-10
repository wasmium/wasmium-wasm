package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirFloatType

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class FloatBinaryExpression(
    public val type: WirFloatType,
    public val operation: FloatBinaryOperation,
    public val first: WirExpression,
    public val second: WirExpression,
) : WirExpression(WirNodeKind.FLOAT_BINARY) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitFloatBinary(this)
    }
}
