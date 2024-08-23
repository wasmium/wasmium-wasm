package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirFloatType

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class LoadFloatExpression(
    public val alignment: Int,
    public override val index: WirExpression,
    public override val offset: Int,
    public val type: WirFloatType,
) : WirExpression(WirNodeKind.LOAD_FLOAT), MemoryAccess {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitLoadFloat(this)
    }
}
