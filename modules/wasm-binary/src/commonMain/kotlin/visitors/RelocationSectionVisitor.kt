package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.SectionKind

public interface RelocationSectionVisitor {
    public fun visitRelocation(relocationKind: RelocationKind, offset: UInt, index: UInt)

    public fun visitRelocation(relocationKind: RelocationKind, offset: UInt, index: UInt, addend: Int)

    public fun visitSection(sectionKind: SectionKind, sectionName: String)

    public fun visitEnd()
}
