package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.sections.RelocationType

public open class RelocationSectionAdapter(protected val delegate: RelocationSectionVisitor? = null) : RelocationSectionVisitor {

    override fun visitRelocation(sectionKind: SectionKind, sectionName: String?, relocationTypes: List<RelocationType>): Unit =
        delegate?.visitRelocation(sectionKind, sectionName, relocationTypes) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
