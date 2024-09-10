package org.wasmium.wir.ast

import org.wasmium.wir.ast.expressions.WirExpression
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class WirElement(
    protected val elements: List<WirFunction>,
    protected val initializer: WirExpression,
) : WirNode(WirNodeKind.ELEMENT) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitElement(this)
    }
}
