package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class BranchExpression(
    public val condition: WirExpression,
    public val target: BlockExpression,
    public val result: WirExpression,
) : WirExpression(WirNodeKind.BRANCH) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitBranch(this)
    }
}
