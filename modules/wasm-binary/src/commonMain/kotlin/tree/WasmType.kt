package org.wasmium.wasm.binary.tree

public enum class WasmType(public val wasmTypeId: UInt) {
    /** No type or empty block. */
    NONE(0x40u),
    /** Value type Int 32. */
    I32(0x7Fu),
    /** Value type Int 64. */
    I64(0x7Eu),
    /** Value type Float 32. */
    F32(0x7Du),
    /** Value type Float 64. */
    F64(0x7Cu),
    /** SIMD Vector type. */
    V128(0x7Bu),
    /** Function with any signature type. */
    ANYFUNC(0x70u),
    /** Function type. */
    FUNCTION(0x60u),
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
        public fun fromWasmTypeId(wasmTypeId: UInt): WasmType? = values().firstOrNull { it.wasmTypeId == wasmTypeId }
    }
}
