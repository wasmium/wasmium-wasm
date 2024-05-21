package org.wasmium.wasm.binary

public object WasmBinary {

    public object Meta {
        /** WASM file magic number. */
        public const val MAGIC_NUMBER: UInt = 0x6d736100u

        /** WASM file version 1. */
        public const val VERSION_1: UInt = 0x01u

        /** WASM file version 2. */
        public const val VERSION_2: UInt = 0x02u
    }

    /** Size of one linear memory page in bytes. This is fixed at 64KiB (64 * 2^10). */
    public const val PAGE_SIZE_BYTES: UInt = 65536u

    /** Maximum number of types. */
    public const val MAX_TYPES: UInt = 1_000_000u

    /** Maximum number of functions. */
    public const val MAX_FUNCTIONS: UInt = 1_000_000u

    /** Maximum number of imports. */
    public const val MAX_IMPORTS: UInt = 100_000u

    /** Maximum number of exports. */
    public const val MAX_EXPORTS: UInt = 100_000u

    /** Maximum number of globals. */
    public const val MAX_GLOBALS: UInt = 1_000_000u

    /** Maximum number of data segments. */
    public const val MAX_DATA_SEGMENTS: UInt = 100_000u

    /** Maximum size of bytes in a data segment. */
    public const val MAX_DATA_SEGMENT_LENGTH: UInt = 10_000_000u

    /** Maximum number of tables. */
    public const val MAX_TABLES: UInt = 1u

    /** Maximum number of table pages. */
    public const val MAX_TABLE_PAGES: UInt = 1_000_000u

    /** Maximum size of bytes in a element segment. */
    public const val MAX_ELEMENT_SEGMENT_LENGTH: UInt = 1_000_000u

    public const val MAX_ELEMENT_SEGMENT_FUNCTION_INDEXES: UInt = 1_000_000u

    /** Maximum number of memories. */
    public const val MAX_MEMORIES: UInt = 1u

    /** Maximum number of memory pages. */
    public const val MAX_MEMORY_PAGES: UInt = 65536u

    /** Maximum size of bytes of a string. */
    public const val MAX_STRING_SIZE: UInt = 100_000u

    /** Maximum size of bytes of a module is 1024 * 1024 * 1024 */
    public const val MAX_MODULE_SIZE: UInt = 1073741824u

    /** Maximum number of function parameters. */
    public const val MAX_FUNCTION_PARAMS: UInt = 1_000u

    /** Maximum number of function results. */
    public const val MAX_FUNCTION_RESULTS: UInt = 100u

    /** Maximum number of function instructions. */
    public const val MAX_FUNCTION_INSTRUCTIONS: UInt = 10_000_000u

    /** Maximum number of function local entries. */
    public const val MAX_FUNCTION_LOCALS: UInt = 50_000u

    /** Maximum number of function locals. Sum of all local counts. */
    public const val MAX_FUNCTION_LOCALS_TOTAL: UInt = 10_000_000u

    /** Maximum size of bytes of a function body is 1024 * 128 * 64 */
    public const val MAX_FUNCTION_SIZE: UInt = 8388608u

    /** Maximum number of element segments. */
    public const val MAX_ELEMENT_SEGMENTS: UInt = 10_000_000u

    /** Maximum number of exceptions. */
    public const val MAX_TAGS: UInt = 1_000_000u

    /** Maximum number of exceptions types. */
    public const val MAX_TAG_TYPES: UInt = 1_000_000u

    public const val MAX_RELOCATIONS: UInt = 1_000_000u

    public const val MAX_NAMES: UInt = 10_000_000u

    public const val MAX_NAMES_LABELS: UInt = 10_000_000u

    public const val MAX_NAMES_FIELDS: UInt = 10_000_000u

    /** Maximum size of bytes of a section. */
    public const val MAX_SECTION_LENGTH: UInt = 100_000_000u

    /** Maximum number of section. */
    public const val MAX_SECTIONS: UInt = 1000u

    public const val ELEMENT_PASSIVE_OR_DECLARATIVE: Int = 1
    public const val ELEMENT_TABLE_INDEX: Int = 1 shl 1
    public const val ELEMENT_EXPRESSIONS: Int = 1 shl 2

    public const val DATA_ACTIVE: Int = 0
    public const val DATA_PASSIVE: Int = 1
    public const val DATA_EXPLICIT: Int = 1 shl 1

    public const val LINKING_SYMBOL_FLAG_UNDEFINED: UInt = 0x10u
    public const val LINKING_SYMBOL_MASK_VISIBILITY: UInt = 0x4u
    public const val LINKING_SYMBOL_MASK_BINDING: UInt = 0x3u
}
