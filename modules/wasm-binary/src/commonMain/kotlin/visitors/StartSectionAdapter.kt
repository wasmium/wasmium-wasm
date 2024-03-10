package org.wasmium.wasm.binary.visitors

public open class StartSectionAdapter (protected val delegate: StartSectionVisitor? = null) : StartSectionVisitor {
    override fun visitStartFunctionIndex(functionIndex: UInt) {
        delegate?.visitStartFunctionIndex(functionIndex)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
