package org.wasmium.wir.ast

import org.wasmium.wir.ast.visitor.WirVisitorVoid

public class WirMemory(
    public val signature: WirMemorySignature,
    public val segments: List<WirData>,
) : WirObject(WirNodeKind.MEMORY) {
    public override fun accept(visitor: WirVisitorVoid) {
        visitor.visitMemory(this)
    }
}
