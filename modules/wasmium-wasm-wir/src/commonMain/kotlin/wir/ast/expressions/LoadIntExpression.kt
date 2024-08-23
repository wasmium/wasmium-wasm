package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirInt32Subtype

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class LoadIntExpression(
    public val alignment: Int,
    public override val index: WirExpression,
    public val convertFrom: WirInt32Subtype,
    public override val offset: Int,
) : WirExpression(WirNodeKind.LOAD_INT), MemoryAccess {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitLoadInt(this)
    }
}
