package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.RelocationSectionVisitor

public class RelocationSectionNode : CustomSectionNode(), RelocationSectionVisitor {
    public var kind: SectionKind? = null
    public var sectionName: String? = null
    public val relocations: MutableList<RelocationNode> = mutableListOf<RelocationNode>()

    public fun accept(relocationSectionVisitor: RelocationSectionVisitor) {
        for (relocation in relocations) {
            relocationSectionVisitor.visitSection(kind!!, sectionName!!)

            if (relocation.addend != null) {
                relocationSectionVisitor.visitRelocation(relocation.relocationKind!!, relocation.offset!!, relocation.index!!, relocation.addend!!)
            } else {
                relocationSectionVisitor.visitRelocation(relocation.relocationKind!!, relocation.offset!!, relocation.index!!)
            }
        }
    }

    public override fun visitSection(sectionKind: SectionKind, sectionName: String) {
        this.kind = sectionKind
        this.sectionName = sectionName
    }

    public override fun visitRelocation(relocationKind: RelocationKind, offset: UInt, index: UInt) {
        val relocation = RelocationNode()
        relocation.relocationKind = relocationKind
        relocation.index = index
        relocation.offset = offset

        relocations.add(relocation)
    }

    public override fun visitRelocation(relocationKind: RelocationKind, offset: UInt, index: UInt, addend: Int) {
        val relocation = RelocationNode()
        relocation.relocationKind = relocationKind
        relocation.index = index
        relocation.offset = offset
        relocation.addend = addend

        relocations.add(relocation)
    }

    public override fun visitEnd() {
        // empty
    }

}
