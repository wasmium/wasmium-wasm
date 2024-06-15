package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class StoreFloatExpression(
    public val alignment: Int,
    public val index: WirExpression,
    public val value: WirExpression,
    public val offset: Int,
) : WirExpression(WirNodeKind.STORE_FLOAT) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitStoreFloat32(this)
    }
}
