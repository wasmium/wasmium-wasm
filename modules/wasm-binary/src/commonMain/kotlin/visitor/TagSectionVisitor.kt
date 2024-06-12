package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.TagType

public interface TagSectionVisitor {
    public fun visitTag(tagType: TagType)

    public fun visitEnd()
}
