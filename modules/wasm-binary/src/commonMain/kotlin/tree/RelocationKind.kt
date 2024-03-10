package org.wasmium.wasm.binary.tree

public enum class RelocationKind(public val relocationKindId: Int) {
    /** Immediate of call instruction. */
    FUNC_INDEX_LEB(0),
    /** Loading address of function. */
    TABLE_INDEX_SLEB(1),
    /** Function address in DATA. */
    TABLE_INDEX_I32(2),
    /** Memory address in load/store offset immediate */
    MEMORY_ADDRESS_LEB(3),
    /** Memory address in i32.const */
    MEMORY_ADDRESS_SLEB(4),
    /** Memory address in DATA */
    MEMORY_ADDRESS_I32(5),
    /** Immediate type in call_indirect. */
    TYPE_INDEX_LEB(6),
    /** Immediate of get_global inst. */
    GLOBAL_INDEX_LEB(7),
    /** Code offset in DWARF metadata. */
    FUNCTION_OFFSET_I32(8),
    /** Section offset in DWARF metadata. */
    SECTION_OFFSET_I32(9),
    /** No RelocationKind */
    NONE(-1),

    // end
    ;

    public companion object {
        public fun fromRelocationKind(relocationKindId: UInt): RelocationKind = RelocationKind.values().find { it.relocationKindId == relocationKindId.toInt() } ?: NONE
    }
}
