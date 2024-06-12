package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.sections.RelocationType

public interface RelocationSectionVisitor {

    public fun visitRelocation(sectionKind: SectionKind, sectionName: String?, relocationTypes: List<RelocationType>)

    public fun visitEnd()
}
