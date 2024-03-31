package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.LinkingSymbolType

public open class LinkingSectionAdapter(protected val delegate: LinkingSectionVisitor? = null) : LinkingSectionVisitor {
    public override fun visitSegment(name: String, alignment: UInt, flags: UInt) {
        delegate?.visitSegment(name, alignment, flags)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }

    public override fun visitDataAlignment(alignment: Long) {
        delegate?.visitDataAlignment(alignment)
    }

    public override fun visitSectionSymbol(flags: UInt, sectionIndex: UInt) {
        delegate?.visitSectionSymbol(flags, sectionIndex)
    }

    public override fun visitSymbol(symbolType: LinkingSymbolType, flags: UInt) {
        delegate?.visitSymbol(symbolType, flags)
    }

    public override fun visitDataSymbol(flags: UInt, name: String, segmentIndex: UInt, offset: UInt, size: UInt) {
        delegate?.visitDataSymbol(flags, name, segmentIndex, offset, size)
    }

    public override fun visitFunctionSymbol(flags: UInt, name: String, functionIndex: UInt) {
        delegate?.visitFunctionSymbol(flags, name, functionIndex)
    }

    public override fun visitGlobalSymbol(flags: UInt, name: String, globalIndex: UInt) {
        delegate?.visitGlobalSymbol(flags, name, globalIndex)
    }
}
