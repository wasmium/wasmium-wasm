package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class ConstantFloat32Expression(private val value: Float) : WirExpression(WirNodeKind.CONST_FLOAT32) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitConstFloat32(this)
    }
}
