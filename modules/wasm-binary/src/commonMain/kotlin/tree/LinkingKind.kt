package org.wasmium.wasm.binary.tree

public enum class LinkingKind(public val linkingKindId: Int) {
    SEGMENT_INFO(5),
    INIT_FUNCS(6),
    COMDAT_INFO(7),
    SYMBOL_TABLE(8),

    /** No LinkingKind */
    NONE(-1),
    ;

    public companion object {
        public fun fromLinkingKindId(linkingKindId: UInt): LinkingKind = values().find { it.linkingKindId == linkingKindId.toInt() } ?: NONE
    }
}
