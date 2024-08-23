package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirValueType

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class ConvertExpression(
    public val sourceType: WirValueType,
    public val targetType: WirValueType,
    public val isSigned: Boolean,
) : WirExpression(WirNodeKind.CONVERT) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitConvert(this)
    }
}
