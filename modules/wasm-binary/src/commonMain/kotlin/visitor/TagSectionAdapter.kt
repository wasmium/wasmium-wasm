package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.TagType

public class TagSectionAdapter(protected val delegate: TagSectionVisitor? = null) : TagSectionVisitor {
    public override fun visitTag(tagType: TagType): Unit = delegate?.visitTag(tagType) ?: Unit

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
