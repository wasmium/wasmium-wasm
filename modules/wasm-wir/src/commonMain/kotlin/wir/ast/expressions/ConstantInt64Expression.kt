package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class ConstantInt64Expression(private val value: Long) : WirExpression(WirNodeKind.CONST_INT64) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitConstInt64(this)
    }
}
