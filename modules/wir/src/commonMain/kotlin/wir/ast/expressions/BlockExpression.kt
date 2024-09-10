package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirValueType
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class BlockExpression(
    public val body: List<WirExpression>,
    public val type: WirValueType,
    public val isLoop: Boolean,
) : WirExpression(WirNodeKind.BLOCK) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitBlock(this)
    }
}
