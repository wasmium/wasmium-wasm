package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class NopExpression : WirExpression(WirNodeKind.NOP) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitNop(this)
    }
}
