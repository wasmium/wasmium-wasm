package org.wasmium.wasm.binary.tree

public enum class LinkingSymbolType(public val linkingSymbolTypeId: Int) {
    FUNCTION(0),
    DATA(1),
    GLOBAL(2),
    SECTION(3),

    /** No LinkingSymbolType. */
    NONE(-1),
    // end
    ;

    public companion object {
        public fun fromLinkingSymbolTypeId(linkingSymbolTypeId: Int): LinkingSymbolType =
            values().find { it.linkingSymbolTypeId == linkingSymbolTypeId } ?: NONE
    }
}
