package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.LinkingSymbolType

public open class LinkingSectionAdapter(protected val delegate: LinkingSectionVisitor? = null) : LinkingSectionVisitor {

    public override fun visitSegment(name: String, alignment: UInt, flags: UInt): Unit = delegate?.visitSegment(name, alignment, flags) ?: Unit

    public override fun visitDataAlignment(alignment: Long): Unit = delegate?.visitDataAlignment(alignment) ?: Unit

    public override fun visitSectionSymbol(flags: UInt, sectionIndex: UInt): Unit = delegate?.visitSectionSymbol(flags, sectionIndex) ?: Unit

    public override fun visitSymbol(symbolType: LinkingSymbolType, flags: UInt): Unit = delegate?.visitSymbol(symbolType, flags) ?: Unit

    public override fun visitDataSymbol(
        flags: UInt,
        name: String,
        segmentIndex: UInt,
        offset: UInt,
        size: UInt
    ): Unit = delegate?.visitDataSymbol(flags, name, segmentIndex, offset, size) ?: Unit

    public override fun visitFunctionSymbol(flags: UInt, name: String, functionIndex: UInt): Unit = delegate?.visitFunctionSymbol(flags, name, functionIndex) ?: Unit

    public override fun visitGlobalSymbol(flags: UInt, name: String, globalIndex: UInt): Unit = delegate?.visitGlobalSymbol(flags, name, globalIndex) ?: Unit

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
