package org.wasmium.wasm.binary.visitors

public class CustomSectionAdapter (private val delegate: CustomSectionVisitor? = null) : CustomSectionVisitor {
    public override fun visitSection(name: String, content: ByteArray) {
        delegate?.visitSection(name, content)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
