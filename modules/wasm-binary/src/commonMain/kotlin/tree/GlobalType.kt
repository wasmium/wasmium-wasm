package org.wasmium.wasm.binary.tree

/**
 * Global variable.
 */
public class GlobalType(
    /** Type of the variable. */
    public val contentType: WasmType,
    /** Whether the variable is mutable. */
    public val mutable: Mutable,
) {
    public enum class Mutable {
        IMMUTABLE,
        MUTABLE
    }
}
