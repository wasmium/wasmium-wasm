package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class UnreachableExpression : WirExpression(WirNodeKind.UNREACHABLE) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitUnreachable(this)
    }
}
