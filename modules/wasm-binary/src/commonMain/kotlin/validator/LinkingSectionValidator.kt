package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.LinkingSymbolType
import org.wasmium.wasm.binary.visitors.LinkingSectionVisitor

public class LinkingSectionValidator(private val delegate: LinkingSectionVisitor, private val context: ValidatorContext) : LinkingSectionVisitor {
    override fun visitSegment(name: String, alignment: UInt, flags: UInt) {
        delegate.visitSegment(name, alignment, flags)
    }

    override fun visitDataAlignment(alignment: Long) {
        delegate.visitDataAlignment(alignment)
    }

    override fun visitSectionSymbol(flags: UInt, sectionIndex: UInt) {
        delegate.visitSectionSymbol(flags, sectionIndex)
    }

    override fun visitSymbol(symbolType: LinkingSymbolType, flags: UInt) {
        delegate.visitSymbol(symbolType, flags)
    }

    override fun visitDataSymbol(flags: UInt, name: String, segmentIndex: UInt, offset: UInt, size: UInt) {
        delegate.visitDataSymbol(flags, name, segmentIndex, offset, size)
    }

    override fun visitFunctionSymbol(flags: UInt, name: String, functionIndex: UInt) {
        delegate.visitFunctionSymbol(flags, name, functionIndex)
    }

    override fun visitGlobalSymbol(flags: UInt, name: String, globalIndex: UInt) {
        delegate.visitGlobalSymbol(flags, name, globalIndex)
    }

    override fun visitEnd() {
        delegate.visitEnd()
    }
}
