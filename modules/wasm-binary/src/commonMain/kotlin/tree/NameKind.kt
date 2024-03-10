package org.wasmium.wasm.binary.tree

public enum class NameKind(public val nameKindId: Int) {
    MODULE(0),
    FUNCTION(1),
    LOCAL(2),
    LABEL(3),
    TABLE(5),
    MEMORY(6),
    GLOBAL(7),
    ELEMENT(8),
    DATA(9),
    TAG(11),

    /** No NameKind */
    NONE(-1),
    ;

    public companion object {
        public fun fromNameKindId(nameKindId: UInt): NameKind = NameKind.values().find { it.nameKindId == nameKindId.toInt() } ?: NONE
    }
}
