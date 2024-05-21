package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.visitors.TagSectionVisitor

public class TagSectionValidator(private val delegate: TagSectionVisitor? = null, private val context: ValidatorContext) : TagSectionVisitor {
    override fun visitTag(tagType: TagType) {
        context.tags.add(tagType)

        delegate?.visitTag(tagType)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
