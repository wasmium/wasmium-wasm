package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class CallExpression(
    public val arguments: List<WirExpression>,
    public val functionName: String,
    public val isImported: Boolean,
) : WirExpression(WirNodeKind.CALL) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitCall(this)
    }
}
