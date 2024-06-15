package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirValueType

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class TruncateExpression(
    public val sourceType: WirValueType,
    public val targetType: WirValueType,
    public val isSigned: Boolean,
    public val isSaturate: Boolean,
) : WirExpression(WirNodeKind.TRUNCATE) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitTruncate(this)
    }
}
