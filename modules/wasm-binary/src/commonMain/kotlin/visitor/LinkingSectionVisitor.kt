package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.LinkingSymbolType

public interface LinkingSectionVisitor {

    public fun visitSegment(name: String, alignment: UInt, flags: UInt)

    public fun visitDataAlignment(alignment: Long)

    public fun visitSectionSymbol(flags: UInt, sectionIndex: UInt)

    public fun visitSymbol(symbolType: LinkingSymbolType, flags: UInt)

    public fun visitDataSymbol(flags: UInt, name: String, segmentIndex: UInt, offset: UInt, size: UInt)

    public fun visitFunctionSymbol(flags: UInt, name: String, functionIndex: UInt)

    public fun visitGlobalSymbol(flags: UInt, name: String, globalIndex: UInt)

    public fun visitEnd()
}
