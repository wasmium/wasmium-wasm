package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.SectionKind

public open class RelocationSectionAdapter(protected val delegate: RelocationSectionVisitor? = null) : RelocationSectionVisitor {

    public override fun visitSection(sectionKind: SectionKind, sectionName: String): Unit = delegate?.visitSection(sectionKind, sectionName) ?: Unit

    public override fun visitRelocation(
        relocationKind: RelocationKind,
        offset: UInt,
        index: UInt,
        addend: Int?
    ): Unit = delegate?.visitRelocation(relocationKind, offset, index, addend) ?: Unit

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
