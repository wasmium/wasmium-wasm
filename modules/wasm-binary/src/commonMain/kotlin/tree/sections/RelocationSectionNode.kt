package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.visitors.RelocationSectionVisitor

public class RelocationSectionNode : CustomSectionNode(SectionName.RELOCATION.sectionName), RelocationSectionVisitor {
    public var kind: SectionKind? = null
    public var sectionName: String? = null
    public val relocations: MutableList<RelocationType> = mutableListOf()

    public fun accept(relocationSectionVisitor: RelocationSectionVisitor) {
        for (relocation in relocations) {
            relocationSectionVisitor.visitSection(kind!!, sectionName!!)

            relocationSectionVisitor.visitRelocation(relocation.relocationKind, relocation.offset, relocation.index, relocation.addend)
        }

        relocationSectionVisitor.visitEnd()
    }

    public override fun visitSection(sectionKind: SectionKind, sectionName: String) {
        this.kind = sectionKind
        this.sectionName = sectionName
    }

    public override fun visitRelocation(relocationKind: RelocationKind, offset: UInt, index: UInt, addend: Int?) {
        relocations.add(RelocationType(relocationKind, offset, index, addend))
    }

    public override fun visitEnd() {
        // empty
    }
}
