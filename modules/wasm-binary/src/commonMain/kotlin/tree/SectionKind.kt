package org.wasmium.wasm.binary.tree

public enum class SectionKind(public val sectionKindId: Int) {
    /** Custom section. */
    CUSTOM(0),
    /** Type section. */
    TYPE(1),
    /** Import section. */
    IMPORT(2),
    /** Function section. */
    FUNCTION(3),
    /** Table section. */
    TABLE(4),
    /** Memory section. */
    MEMORY(5),
    /** Global section. */
    GLOBAL(6),
    /** Exports section. */
    EXPORT(7),
    /** Start function section . */
    START(8),
    /** Elements segment section. */
    ELEMENT(9),
    /** Code section. */
    CODE(10),
    /** Data segment section. */
    DATA(11),
    /** No Section. */
    NONE(-1),
    ;

    public companion object {
        public fun fromSectionKindId(sectionKindId: UInt): SectionKind = values().find { it.sectionKindId == sectionKindId.toInt() } ?: NONE
    }
}
