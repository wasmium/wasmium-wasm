package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.LinkingSymbolType

public class LinkingSectionAdapter(protected val delegate: LinkingSectionVisitor? = null) : LinkingSectionVisitor {
    public override fun visitSegment(name: String, alignment: UInt, flags: UInt) {
        delegate?.visitSegment(name, alignment, flags)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }

    public override fun visitDataAlignment(alignment: Long) {
        delegate?.visitDataAlignment(alignment)
    }

    public override fun visitSectionSymbol(index: UInt, flags: UInt, sectionIndex: UInt) {
        delegate?.visitSectionSymbol(index, flags, sectionIndex)
    }

    public override fun visitSymbol(index: UInt, symbolType: LinkingSymbolType, flags: UInt) {
        delegate?.visitSymbol(index, symbolType, flags)
    }

    public override fun visitDataSymbol(index: UInt, flags: UInt, name: String, segmentIndex: UInt, offset: UInt, size: UInt) {
        delegate?.visitDataSymbol(index, flags, name, segmentIndex, offset, size)
    }

    public override fun visitFunctionSymbol(index: UInt, flags: UInt, name: String, functionIndex: UInt) {
        delegate?.visitFunctionSymbol(index, flags, name, functionIndex)
    }

    public override fun visitGlobalSymbol(index: UInt, flags: UInt, name: String, globalIndex: UInt) {
        delegate?.visitGlobalSymbol(index, flags, name, globalIndex)
    }
}
