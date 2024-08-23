package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirInt32Subtype

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class StoreIntExpression(
    public val alignment: Int,
    public val index: WirExpression,
    public val value: WirExpression,
    public val convertTo: WirInt32Subtype,
    public val offset: Int,
) : WirExpression(WirNodeKind.STORE_INT) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitStoreInt32(this)
    }
}
