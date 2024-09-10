package org.wasmium.wasm.binary.tree

public enum class LinkingSymbolType(public val linkingSymbolTypeId: UInt) {
    FUNCTION(0u),
    DATA(1u),
    GLOBAL(2u),
    SECTION(3u),
    // end
    ;

    public companion object {
        public fun fromLinkingSymbolTypeId(linkingSymbolTypeId: UInt): LinkingSymbolType? = values().firstOrNull { it.linkingSymbolTypeId == linkingSymbolTypeId }
    }
}
