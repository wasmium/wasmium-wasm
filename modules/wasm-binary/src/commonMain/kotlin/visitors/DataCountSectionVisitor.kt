package org.wasmium.wasm.binary.visitors

public interface DataCountSectionVisitor {
    public fun visitDataCount(count: UInt)

    public fun visitEnd()
}
