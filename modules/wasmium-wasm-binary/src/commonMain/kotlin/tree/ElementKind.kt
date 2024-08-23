package org.wasmium.wasm.binary.tree

public enum class ElementKind(public val elementKindId: UInt) {
    FUNCTION_REF(0u),
    // end
    ;

    public companion object {
        public fun fromElementKind(elementKindId: UInt): ElementKind? = values().firstOrNull { it.elementKindId == elementKindId }
    }
}
