package org.wasmium.wasm.binary.visitors

public open class NameSectionAdapter (protected val delegate: NameSectionVisitor? = null) : NameSectionVisitor {
    public override fun visitModuleName(name: String) {
        delegate?.visitModuleName(name)
    }

    public override fun visitLocalName(functionIndex: UInt, localIndex: UInt, name: String) {
        delegate?.visitLocalName(functionIndex, localIndex, name)
    }

    override fun visitLabelName(functionIndex: UInt, nameLocalIndex: UInt, localName: String) {
        delegate?.visitLabelName(functionIndex, nameLocalIndex, localName)
    }

    public override fun visitFunctionName(functionIndex: UInt, name: String) {
        delegate?.visitFunctionName(functionIndex, name)
    }

    override fun visitGlobalName(functionIndex: UInt, name: String) {
        delegate?.visitGlobalName(functionIndex, name)
    }

    override fun visitTagName(functionIndex: UInt, functionName: String) {
        delegate?.visitTagName(functionIndex, functionName)
    }

    override fun visitTableName(functionIndex: UInt, functionName: String) {
        delegate?.visitTableName(functionIndex, functionName)
    }

    override fun visitMemoryName(functionIndex: UInt, functionName: String) {
        delegate?.visitMemoryName(functionIndex, functionName)
    }

    override fun visitElementName(functionIndex: UInt, functionName: String) {
        delegate?.visitElementName(functionIndex, functionName)
    }

    override fun visitDataName(functionIndex: UInt, functionName: String) {
        delegate?.visitDataName(functionIndex, functionName)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
