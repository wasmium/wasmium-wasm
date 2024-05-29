package org.wasmium.wasm.binary.tree

/**
 * Custom section names.
 */
public enum class SectionName(public val sectionName: String) {
    /** Name of the custom section "name" */
    NAME("name"),

    /** Name of the custom section "reloc" */
    RELOCATION("reloc"),

    /** Name of the custom section "linking" */
    LINKING("linking"),

    /** Name of the custom section "sourceMappingURL" */
    SOURCE_MAPPING_URL("sourceMappingURL"),

    EXTERNAL_DEBUG_INFO("external_debug_info"),
    ;

    public companion object {
        public fun fromCustomSectionKindId(sectionKindId: String): SectionName? = values().firstOrNull { it.sectionName == sectionKindId }
    }
}

