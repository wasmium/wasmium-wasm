package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirValueType
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class ConditionalExpression(
    public val condition: WirExpression,
    public val thenBlock: BlockExpression,
    public val elseBlock: BlockExpression,
    public val type: WirValueType,
) : WirExpression(WirNodeKind.IF) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitConditional(this)
    }
}
