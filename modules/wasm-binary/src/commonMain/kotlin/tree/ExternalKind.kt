package org.wasmium.wasm.binary.tree

public enum class ExternalKind(public val externalKindId: Int) {
    /** External function. */
    FUNCTION(0),
    /** External table. */
    TABLE(1),
    /** External memory. */
    MEMORY(2),
    /** External global. */
    GLOBAL(3),
    /** External exception. */
    EXCEPTION(4),
    /** No ExternalKind */
    NONE(-1),
    // end
    ;

    public companion object {
        public fun fromExternalKindId(externalKindId: UInt): ExternalKind = values().find { it.externalKindId == externalKindId.toInt() } ?: NONE
    }
}
