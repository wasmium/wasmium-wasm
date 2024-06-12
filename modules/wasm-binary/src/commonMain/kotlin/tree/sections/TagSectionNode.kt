package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.visitor.TagSectionVisitor

public class TagSectionNode : SectionNode(SectionKind.TAG), TagSectionVisitor {
    public val tagTypes: MutableList<TagType> = mutableListOf()

    public fun accept(tagSectionVisitor: TagSectionVisitor) {
        for (tagType in tagTypes) {
            tagSectionVisitor.visitTag(tagType)
        }

        tagSectionVisitor.visitEnd()
    }

    public override fun visitTag(tagType: TagType) {
        tagTypes.add(tagType)
    }

    public override fun visitEnd() {
        // empty
    }
}
