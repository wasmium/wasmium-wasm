package org.wasmium.wasm.binary.tree

public enum class WasmType(public val wasmTypeId: UInt) {
    // value types
    I32(0x7Fu),
    I64(0x7Eu),
    F32(0x7Du),
    F64(0x7Cu),
    V128(0x7Bu),

    // packed types
    I8(0x78u),
    I16(0x77u),

    // reference types
    NULL_FUNC_REF(0x73u),
    NULL_EXTERN_REF(0x72u),
    NULL_REF(0x71u),
    I31_REF(0x6cu),
    STRUCT_REF(0x6bu),
    ARRAY_REF(0x6au),
    FUNC_REF(0x70u),
    EXTERN_REF(0x6fu),
    ANY_REF(0x6eu),
    EQ_REF(0x6du),
    NON_NULLABLE(0x64u),
    NULLABLE(0x63u),

    // exception handling
    EXN_REF(0x69u),
    NULL_EXN_REF(0x74u),

    // string reference types
    STRING_REF(0x67u),
    STRING_VIEW_WTF8(0x66u),
    STRING_VIEW_WTF16(0x62u),
    STRING_VIEW_ITER(0x61u),

    // type forms
    FUNC(0x60u),
    CONT(0x5du),
    STRUCT(0x5fu),
    ARRAY(0x5eu),
    SUB(0x50u),
    SUB_FINAL(0x4fu),

    // iso recursive recursion groups
    REC(0x4eu),

    // block_type
    NONE(0x40u),
    // end
    ;

    public fun isNumberType(): Boolean = when (this) {
        I32,
        I64,
        F32,
        F64 -> true

        else -> false
    }

    public fun isValueType(): Boolean = isNumberType() || isReferenceType() || isVectorType()

    public fun isReferenceType(): Boolean = when (this) {
        FUNC_REF,
        EXTERN_REF -> true

        else -> false
    }

    public fun isVectorType(): Boolean = this == V128

    public companion object {
        public fun fromWasmTypeId(wasmTypeId: UInt): WasmType? = values().firstOrNull { it.wasmTypeId == wasmTypeId }
    }
}
