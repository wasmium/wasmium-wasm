package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.SectionKind

public open class RelocationSectionAdapter(protected val delegate: RelocationSectionVisitor? = null) : RelocationSectionVisitor {

    override fun visitSection(sectionKind: SectionKind, sectionName: String): Unit = delegate?.visitSection(sectionKind, sectionName) ?: Unit

    override fun visitRelocation(relocationKind: RelocationKind, offset: UInt, index: UInt, addend: Int?): Unit =
        delegate?.visitRelocation(relocationKind, offset, index, addend) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
