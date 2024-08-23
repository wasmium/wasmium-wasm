package org.wasmium.wir.ast

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class WirTable(
    /** Signature of the table.  */
    protected val signature: WirTableSignature,
    /** List of elements of the table.  */
    protected val elements: List<WirElement>,
) : WirObject(WirNodeKind.TABLE) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitTable(this)
    }
}
