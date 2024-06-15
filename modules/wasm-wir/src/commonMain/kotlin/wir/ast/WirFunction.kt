package org.wasmium.wir.ast

import org.wasmium.wir.ast.expressions.WirExpression
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class WirFunction(
    public val locals: List<WirLocal>,
    public val body: List<WirExpression>,
    public val signature: WirFunctionSignature,
    public val isStart: Boolean,
) : WirObject(WirNodeKind.FUNCTION) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitFunction(this)
    }
}
