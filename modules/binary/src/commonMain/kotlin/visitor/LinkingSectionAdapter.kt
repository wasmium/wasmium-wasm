package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.LinkingSymbolType

public open class LinkingSectionAdapter(protected val delegate: LinkingSectionVisitor? = null) : LinkingSectionVisitor {

    override fun visitSegment(name: String, alignment: UInt, flags: UInt): Unit = delegate?.visitSegment(name, alignment, flags) ?: Unit

    override fun visitDataAlignment(alignment: Long): Unit = delegate?.visitDataAlignment(alignment) ?: Unit

    override fun visitSectionSymbol(flags: UInt, sectionIndex: UInt): Unit = delegate?.visitSectionSymbol(flags, sectionIndex) ?: Unit

    override fun visitSymbol(symbolType: LinkingSymbolType, flags: UInt): Unit = delegate?.visitSymbol(symbolType, flags) ?: Unit

    override fun visitDataSymbol(flags: UInt, name: String, segmentIndex: UInt, offset: UInt, size: UInt): Unit =
        delegate?.visitDataSymbol(flags, name, segmentIndex, offset, size) ?: Unit

    override fun visitFunctionSymbol(flags: UInt, name: String, functionIndex: UInt): Unit = delegate?.visitFunctionSymbol(flags, name, functionIndex) ?: Unit

    override fun visitGlobalSymbol(flags: UInt, name: String, globalIndex: UInt): Unit = delegate?.visitGlobalSymbol(flags, name, globalIndex) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
