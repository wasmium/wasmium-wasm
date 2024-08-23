package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class ConstantInt32Expression(private val value: Int) : WirExpression(WirNodeKind.CONST_INT32) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitConstInt32(this)
    }
}
