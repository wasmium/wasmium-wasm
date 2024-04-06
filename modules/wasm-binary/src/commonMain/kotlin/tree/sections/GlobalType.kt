package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.WasmType

/**
 * Global variable.
 */
public class GlobalType(
    /** Type of the variable.  */
    public val contentType: WasmType,
    /** Whether the variable is mutable.  */
    public val isMutable: Boolean,
)
