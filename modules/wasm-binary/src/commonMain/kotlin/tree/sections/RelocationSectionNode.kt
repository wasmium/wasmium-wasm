package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.visitors.RelocationSectionVisitor

public class RelocationSectionNode : CustomSectionNode(SectionName.RELOCATION.sectionName), RelocationSectionVisitor {
    public var kind: SectionKind? = null
    public var sectionName: String? = null
    public val relocations: MutableList<RelocationType> = mutableListOf()

    public fun accept(relocationSectionVisitor: RelocationSectionVisitor) {
        for (relocation in relocations) {
            relocationSectionVisitor.visitRelocation(kind!!, sectionName!!, relocations)
        }

        relocationSectionVisitor.visitEnd()
    }

    public override fun visitRelocation(sectionKind: SectionKind, sectionName: String?, relocationTypes: List<RelocationType>) {
        this.kind = sectionKind
        this.sectionName = sectionName
        this.relocations.addAll(relocationTypes)
    }

    public override fun visitEnd() {
        // empty
    }
}
