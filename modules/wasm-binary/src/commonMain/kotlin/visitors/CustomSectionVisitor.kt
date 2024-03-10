package org.wasmium.wasm.binary.visitors

public interface CustomSectionVisitor {
    public fun visitSection(name: String, content: ByteArray)

    public fun visitEnd()
}
