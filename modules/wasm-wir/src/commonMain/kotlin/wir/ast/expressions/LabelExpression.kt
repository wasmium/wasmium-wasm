package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class LabelExpression(public val type: LabelType) : WirExpression(WirNodeKind.LABEL) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitLabel(this)
    }
}
