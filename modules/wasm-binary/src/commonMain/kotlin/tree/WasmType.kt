package org.wasmium.wasm.binary.tree

public enum class WasmType(public val wasmTypeId: Int) {
    /** No type or empty block. */
    NONE(0x40),

    /** Value type Int 32. */
    I32(0x7f),

    /** Value type Int 64. */
    I64(0x7e),

    /** Value type Float 32. */
    F32(0x7d),

    /** Value type Float 64. */
    F64(0x7c),

    /** SIMD Vector type. */
    V128(0x7b),

    /** Function with any signature type. */
    ANYFUNC(0x70),

    /** Function type. */
    FUNCTION(0x60),

    // end
    ;

    public fun isValueType(): Boolean = when (this) {
        I32,
        I64,
        F32,
        F64,
        V128 -> true

        else -> false
    }

    public fun isElementType(): Boolean = when (this) {
        ANYFUNC -> true
        else -> false
    }

    public fun isInlineType(): Boolean = isValueType() || this == NONE

    public companion object {
        public fun fromWasmTypeId(wasmTypeId: UInt): WasmType = values().find { it.wasmTypeId == wasmTypeId.toInt() } ?: NONE
    }
}
