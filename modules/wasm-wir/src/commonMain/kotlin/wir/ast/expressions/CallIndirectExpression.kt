package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirValueType
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class CallIndirectExpression(
    public val parameterTypes: List<WirValueType>,
    public val returnTypes: List<WirValueType>,
    public val selector: WirExpression,
    public val arguments: List<WirExpression>,
) : WirExpression(WirNodeKind.CALL_INDIRECT) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitCallIndirect(this)
    }
}
