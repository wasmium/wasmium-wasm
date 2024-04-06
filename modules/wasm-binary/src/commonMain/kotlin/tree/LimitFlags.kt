package org.wasmium.wasm.binary.tree

public enum class LimitFlags(public val flag: UInt) {
    /** Empty flags. */
    NONE(0u),

    /** Limits with maximum value. */
    HAS_MAX(1u),

    /** Shared limits. */
    IS_SHARED(2u),
    // end
    ;

    public companion object {
        public fun fromLimitFlags(flags: UInt): LimitFlags = values().find { it.flag == flags } ?: NONE
    }
}
