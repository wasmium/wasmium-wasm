package org.wasmium.wasm.binary.tree

public enum class ExternalKind(public val externalKindId: UInt) {
    FUNCTION(0u),
    TABLE(1u),
    MEMORY(2u),
    GLOBAL(3u),
    TAG(4u),
    // end
    ;

    public companion object {
        public fun fromExternalKindId(externalKindId: UInt): ExternalKind? = values().firstOrNull { it.externalKindId == externalKindId }
    }
}
