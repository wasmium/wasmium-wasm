package org.wasmium.wasm.binary.tree

public enum class NameKind(public val nameKindId: UInt) {
    MODULE(0u),
    FUNCTION(1u),
    LOCAL(2u),
    LABEL(3u),
    TYPE(4u),
    TABLE(5u),
    MEMORY(6u),
    GLOBAL(7u),
    ELEMENT(8u),
    DATA(9u),
    FIELD(10u),
    TAG(11u),

    // end
    ;

    public companion object {
        public fun fromNameKindId(nameKindId: UInt): NameKind? = NameKind.values().firstOrNull { it.nameKindId == nameKindId }
    }
}
