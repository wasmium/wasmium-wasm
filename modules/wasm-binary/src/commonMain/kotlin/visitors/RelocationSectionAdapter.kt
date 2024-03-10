package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.SectionKind

public open class RelocationSectionAdapter (protected val delegate: RelocationSectionVisitor? = null) : RelocationSectionVisitor {
    public override fun visitSection(sectionKind: SectionKind, sectionName: String) {
        delegate?.visitSection(sectionKind, sectionName)
    }

    public override fun visitRelocation(relocationKind: RelocationKind, offset: UInt, index: UInt) {
        delegate?.visitRelocation(relocationKind, offset, index)
    }

    public override fun visitRelocation(relocationKind: RelocationKind, offset: UInt, index: UInt, addend: Int) {
        delegate?.visitRelocation(relocationKind, offset, index, addend)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
