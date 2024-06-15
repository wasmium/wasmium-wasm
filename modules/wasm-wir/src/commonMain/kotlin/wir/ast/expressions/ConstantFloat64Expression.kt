package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class ConstantFloat64Expression(private val value: Double) : WirExpression(WirNodeKind.CONST_FLOAT64) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitConstFloat64(this)
    }
}
