package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.WasmType

/**
 * Global variable.
 */
public class GlobalTypeNode {
    /** Type of the variable.  */
    public var contentType: WasmType? = null

    /** Whether the variable is mutable.  */
    public var isMutable: Boolean = false
}
