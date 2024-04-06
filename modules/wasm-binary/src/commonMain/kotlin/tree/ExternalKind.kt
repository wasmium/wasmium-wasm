package org.wasmium.wasm.binary.tree

public enum class ExternalKind(public val externalKindId: UInt) {
    FUNCTION(0u),
    TABLE(1u),
    MEMORY(2u),
    GLOBAL(3u),
    EXCEPTION(4u),
    MODULE(5u),
    INSTANCE(6u),
    TYPE(7u),
    // end
    ;

    public companion object {
        public fun fromExternalKindId(externalKindId: UInt): ExternalKind? = values().firstOrNull { it.externalKindId == externalKindId }
    }
}
