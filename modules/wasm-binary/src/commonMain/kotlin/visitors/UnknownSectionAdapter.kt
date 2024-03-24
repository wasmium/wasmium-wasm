package org.wasmium.wasm.binary.visitors

public open class UnknownSectionAdapter(private val delegate: UnknownSectionVisitor? = null) : UnknownSectionVisitor {
    public override fun visitSection(name: String, content: ByteArray) {
        delegate?.visitSection(name, content)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
