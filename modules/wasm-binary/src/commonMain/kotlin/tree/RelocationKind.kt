package org.wasmium.wasm.binary.tree

public enum class RelocationKind(public val relocationKindId: UInt) {
    /** Immediate of call instruction. */
    FUNC_INDEX_LEB(0u),
    /** Loading address of function. */
    TABLE_INDEX_SLEB(1u),
    /** Function address in DATA. */
    TABLE_INDEX_I32(2u),
    /** Memory address in load/store offset immediate */
    MEMORY_ADDRESS_LEB(3u),
    /** Memory address in i32.const */
    MEMORY_ADDRESS_SLEB(4u),
    /** Memory address in DATA */
    MEMORY_ADDRESS_I32(5u),
    /** Immediate type in call_indirect. */
    TYPE_INDEX_LEB(6u),
    /** Immediate of get_global inst. */
    GLOBAL_INDEX_LEB(7u),
    /** Code offset in DWARF metadata. */
    FUNCTION_OFFSET_I32(8u),
    /** Section offset in DWARF metadata. */
    SECTION_OFFSET_I32(9u),
    // end
    ;

    public companion object {
        public fun fromRelocationKind(relocationKindId: UInt): RelocationKind? = values().firstOrNull { it.relocationKindId == relocationKindId }
    }
}
