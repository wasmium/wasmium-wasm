package org.wasmium.wasm.binary.visitors

public open class StartSectionAdapter(protected val delegate: StartSectionVisitor? = null) : StartSectionVisitor {
    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
