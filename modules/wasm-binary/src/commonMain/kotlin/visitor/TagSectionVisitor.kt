package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.TagType

public interface TagSectionVisitor {
    public fun visitTag(tagType: TagType)

    public fun visitEnd()
}
