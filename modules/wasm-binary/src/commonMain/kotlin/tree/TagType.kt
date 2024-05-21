package org.wasmium.wasm.binary.tree

public class TagType(
    public val attribute: TagAttribute,
    public val index: UInt,
) {
    public enum class TagAttribute(public val attributeId: UInt){
        EXCEPTION(0u);

        public companion object {
            public fun fromTagAttributeId(attributeId: UInt): TagAttribute? = values().firstOrNull { it.attributeId == attributeId }
        }
    }
}
