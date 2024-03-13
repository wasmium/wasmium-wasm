package org.wasmium.wasm.binary.tree

public enum class LinkingKind(public val linkingKindId: UInt) {
    SEGMENT_INFO(5u),
    INIT_FUNCS(6u),
    COMDAT_INFO(7u),
    SYMBOL_TABLE(8u),
    // end
    ;

    public companion object {
        public fun fromLinkingKindId(linkingKindId: UInt): LinkingKind? = values().firstOrNull { it.linkingKindId == linkingKindId }
    }
}
