package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirValueType

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class ReinterpretExpression(
    public val sourceType: WirValueType,
    public val targetType: WirValueType,
) : WirExpression(WirNodeKind.REINTERPRET) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitReinterpret(this)
    }
}
