package org.wasmium.wasm.binary.tree

public class BlockType(
    public val kind: BlockTypeKind,
    public val value: Int,
) {
    public fun isValueType(): Boolean = kind == BlockTypeKind.VALUE_TYPE

    public fun isFunctionType(): Boolean = kind == BlockTypeKind.FUNCTION_TYPE

    public enum class BlockTypeKind {
        VALUE_TYPE,
        FUNCTION_TYPE,
    }
}
