package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.LinkingSymbolType
import org.wasmium.wasm.binary.visitors.LinkingSectionVisitor

public class LinkingSectionNode : CustomSectionNode(), LinkingSectionVisitor {
    public val symbolInfos: List<SymbolTableLinkingNode> = mutableListOf()
    public val segments: List<SegmentInfoLinkingNode> = mutableListOf()

    public fun accept(linkingSectionVisitor: LinkingSectionVisitor) {
        // TODO
    }

    public override fun visitSegment(name: String, alignment: UInt, flags: UInt) {
        // TODO
    }

    public override fun visitDataAlignment(alignment: Long) {
        // TODO
    }

    override fun visitSectionSymbol(index: UInt, flags: UInt, sectionIndex: UInt) {
        // TODO
    }

    public override fun visitSymbol(index: UInt, symbolType: LinkingSymbolType, flags: UInt) {
        // TODO
    }

    public override fun visitDataSymbol(index: UInt, flags: UInt, name: String, segmentIndex: UInt, offset: UInt, size: UInt) {
        // TODO
    }

    public override fun visitFunctionSymbol(index: UInt, flags: UInt, name: String, functionIndex: UInt) {
        // TODO
    }

    public override fun visitGlobalSymbol(index: UInt, flags: UInt, name: String, globalIndex: UInt) {
        // TODO
    }

    public override fun visitEnd() {
        // empty
    }
}
