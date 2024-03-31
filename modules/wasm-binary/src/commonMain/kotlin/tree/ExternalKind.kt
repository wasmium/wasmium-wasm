package org.wasmium.wasm.binary.tree

public enum class ExternalKind(public val externalKindId: UInt) {
    /** External function. */
    FUNCTION(0u),

    /** External table. */
    TABLE(1u),

    /** External memory. */
    MEMORY(2u),

    /** External global. */
    GLOBAL(3u),

    /** External exception. */
    EXCEPTION(4u),
    // end
    ;

    public companion object {
        public fun fromExternalKindId(externalKindId: UInt): ExternalKind? = values().firstOrNull { it.externalKindId == externalKindId }
    }
}
