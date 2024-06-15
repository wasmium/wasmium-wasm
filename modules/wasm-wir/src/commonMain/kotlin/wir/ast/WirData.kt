package org.wasmium.wir.ast

import org.wasmium.wir.ast.expressions.WirExpression
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class WirData(
    public val initializer: WirExpression,
    public val data: ByteArray,
) : WirNode(WirNodeKind.DATA) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitData(this)
    }
}
