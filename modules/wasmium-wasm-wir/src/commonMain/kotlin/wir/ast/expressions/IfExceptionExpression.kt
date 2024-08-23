package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.type.WirValueType

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class IfExceptionExpression(
    private val block: BlockExpression,
    private val catches: List<BlockExpression>,
    private val type: WirValueType,
) : WirExpression(WirNodeKind.IF_EXCEPT) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitIfException(this)
    }
}
