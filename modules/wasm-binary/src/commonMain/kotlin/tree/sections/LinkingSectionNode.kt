package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.LinkingSymbolType
import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.visitors.LinkingSectionVisitor

public class LinkingSectionNode : CustomSectionNode(SectionName.LINKING.sectionName), LinkingSectionVisitor {
    public val symbolInfos: MutableList<SymbolTableLinkingNode> = mutableListOf()
    public val segments: MutableList<SegmentInfoLinkingNode> = mutableListOf()

    public fun accept(linkingSectionVisitor: LinkingSectionVisitor) {
        // TODO

        linkingSectionVisitor.visitEnd()
    }

    public override fun visitSegment(name: String, alignment: UInt, flags: UInt) {
        // TODO
    }

    public override fun visitDataAlignment(alignment: Long) {
        // TODO
    }

    public override fun visitSectionSymbol(flags: UInt, sectionIndex: UInt) {
        // TODO
    }

    public override fun visitSymbol(symbolType: LinkingSymbolType, flags: UInt) {
        // TODO
    }

    public override fun visitDataSymbol(flags: UInt, name: String, segmentIndex: UInt, offset: UInt, size: UInt) {
        // TODO
    }

    public override fun visitFunctionSymbol(flags: UInt, name: String, functionIndex: UInt) {
        // TODO
    }

    public override fun visitGlobalSymbol(flags: UInt, name: String, globalIndex: UInt) {
        // TODO
    }

    public override fun visitEnd() {
        // empty
    }
}
