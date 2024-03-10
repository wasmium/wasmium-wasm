package org.wasmium.wasm.binary.tree.sections

public enum class NameNodeKind(public val nameNodeKindId: Int) {
    MODULE(0),
    FUNCTION(1),
    LOCAL(2),
    EXCEPTION(3),
    /** No NameNodeKind. */
    NONE(-1),
    ;

    public companion object {
        public fun fromNameNodeKind(nameNodeKindId: Int): NameNodeKind = NameNodeKind.values().find { it.nameNodeKindId == nameNodeKindId } ?: NONE
    }
}
