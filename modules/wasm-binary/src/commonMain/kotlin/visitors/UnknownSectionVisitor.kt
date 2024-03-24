package org.wasmium.wasm.binary.visitors

public interface UnknownSectionVisitor {
    public fun visitSection(name: String, content: ByteArray)

    public fun visitEnd()
}
