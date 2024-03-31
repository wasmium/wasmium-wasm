package org.wasmium.wasm.binary.tree

public enum class SectionKind(public val sectionKindId: UInt) {
    /** Custom section. */
    CUSTOM(0u),

    /** Type section. */
    TYPE(1u),

    /** Import section. */
    IMPORT(2u),

    /** Function section. */
    FUNCTION(3u),

    /** Table section. */
    TABLE(4u),

    /** Memory section. */
    MEMORY(5u),

    /** Global section. */
    GLOBAL(6u),

    /** Exports section. */
    EXPORT(7u),

    /** Start function section. */
    START(8u),

    /** Elements segment section. */
    ELEMENT(9u),

    /** Data count section. */
    DATA_COUNT(12u),

    /** Code section. */
    CODE(10u),

    /** Data segment section. */
    DATA(11u),
    // end
    ;

    public companion object {
        public fun fromSectionKindId(sectionKindId: UInt): SectionKind? = values().firstOrNull { it.sectionKindId == sectionKindId }
    }
}
