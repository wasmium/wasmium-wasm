package org.wasmium.wir.ast.expressions

public interface MemoryAccess {
    public val index: WirExpression
    public val offset: Int
}
