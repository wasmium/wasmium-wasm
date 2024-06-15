package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirValueType

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class DemoteExpression(
    public val sourceType: WirValueType,
    public val targetType: WirValueType,
) : WirExpression(WirNodeKind.DEMOTE) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitDemote(this)
    }
}
