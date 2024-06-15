package org.wasmium.wir.ast.visitor

public interface WirVisitable {
    public fun accept(visitor: WirVisitorVoid)
}
