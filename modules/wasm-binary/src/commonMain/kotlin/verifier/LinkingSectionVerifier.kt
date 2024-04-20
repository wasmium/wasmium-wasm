package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.tree.LinkingSymbolType
import org.wasmium.wasm.binary.visitors.LinkingSectionVisitor

public class LinkingSectionVerifier(private val delegate: LinkingSectionVisitor? = null, private val context: VerifierContext) : LinkingSectionVisitor {
    private var done: Boolean = false

    override fun visitSegment(name: String, alignment: UInt, flags: UInt) {
        checkEnd()

        delegate?.visitSegment(name, alignment, flags)
    }

    override fun visitDataAlignment(alignment: Long) {
        checkEnd()

        delegate?.visitDataAlignment(alignment)
    }

    override fun visitSectionSymbol(flags: UInt, sectionIndex: UInt) {
        checkEnd()

        delegate?.visitSectionSymbol(flags, sectionIndex)
    }

    override fun visitSymbol(symbolType: LinkingSymbolType, flags: UInt) {
        checkEnd()

        delegate?.visitSymbol(symbolType, flags)
    }

    override fun visitDataSymbol(flags: UInt, name: String, segmentIndex: UInt, offset: UInt, size: UInt) {
        checkEnd()

        delegate?.visitDataSymbol(flags, name, segmentIndex, offset, size)
    }

    override fun visitFunctionSymbol(flags: UInt, name: String, functionIndex: UInt) {
        checkEnd()

        delegate?.visitFunctionSymbol(flags, name, functionIndex)
    }

    override fun visitGlobalSymbol(flags: UInt, name: String, globalIndex: UInt) {
        checkEnd()

        delegate?.visitGlobalSymbol(flags, name, globalIndex)
    }

    override fun visitEnd() {
        checkEnd()

        done = true
        delegate?.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
