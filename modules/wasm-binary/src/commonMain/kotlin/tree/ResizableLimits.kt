package org.wasmium.wasm.binary.tree

/**
 * Limits of a table or memory.
 */
public class ResizableLimits(
    /** Initial value */
    public val initial: UInt,
    /** Maximum value */
    public val maximum: UInt? = null,
    /** Flags */
    public val flags: UInt,
) {
    public fun isShared(): Boolean {
        return (flags and LimitFlags.IS_SHARED.flag) != 0.toUInt()
    }
}
