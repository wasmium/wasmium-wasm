package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class BranchTableExpression(
    public val targets: List<BlockExpression>,
    public val defaultTarget: BlockExpression,
    public val index: WirExpression,
) : WirExpression(WirNodeKind.BRANCH_TABLE) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitBranchTable(this)
    }
}
