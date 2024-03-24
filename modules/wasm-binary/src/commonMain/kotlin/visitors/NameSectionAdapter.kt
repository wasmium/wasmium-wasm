package org.wasmium.wasm.binary.visitors

public open class NameSectionAdapter(protected val delegate: NameSectionVisitor? = null) : NameSectionVisitor {
    public override fun visitModuleName(name: String) {
        delegate?.visitModuleName(name)
    }

    public override fun visitLocalName(functionIndex: UInt, localIndex: UInt, name: String) {
        delegate?.visitLocalName(functionIndex, localIndex, name)
    }

    override fun visitLabelName(functionIndex: UInt, nameLocalIndex: UInt, name: String) {
        delegate?.visitLabelName(functionIndex, nameLocalIndex, name)
    }

    public override fun visitFunctionName(functionIndex: UInt, name: String) {
        delegate?.visitFunctionName(functionIndex, name)
    }

    override fun visitGlobalName(functionIndex: UInt, name: String) {
        delegate?.visitGlobalName(functionIndex, name)
    }

    override fun visitTagName(functionIndex: UInt, name: String) {
        delegate?.visitTagName(functionIndex, name)
    }

    override fun visitTableName(functionIndex: UInt, name: String) {
        delegate?.visitTableName(functionIndex, name)
    }

    override fun visitMemoryName(functionIndex: UInt, name: String) {
        delegate?.visitMemoryName(functionIndex, name)
    }

    override fun visitElementName(functionIndex: UInt, name: String) {
        delegate?.visitElementName(functionIndex, name)
    }

    override fun visitDataName(functionIndex: UInt, name: String) {
        delegate?.visitDataName(functionIndex, name)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
