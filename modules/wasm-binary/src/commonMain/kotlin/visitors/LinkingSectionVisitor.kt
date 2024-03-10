package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.LinkingSymbolType

public interface LinkingSectionVisitor {
    public fun visitSegment(name: String, alignment: UInt, flags: UInt)

    public fun visitDataAlignment(alignment: Long)

    public fun visitSectionSymbol(index: UInt, flags: UInt, sectionIndex: UInt)

    public fun visitSymbol(index: UInt, symbolType: LinkingSymbolType, flags: UInt)

    public fun visitDataSymbol(index: UInt, flags: UInt, name: String, segmentIndex: UInt, offset: UInt, size: UInt)

    public fun visitFunctionSymbol(index: UInt, flags: UInt, name: String, functionIndex: UInt)

    public fun visitGlobalSymbol(index: UInt, flags: UInt, name: String, globalIndex: UInt)

    public fun visitEnd()
}
