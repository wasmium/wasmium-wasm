package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.WasmType

/**
 * Global variable.
 */
public class GlobalTypeNode {
    /**
     * Get the type of the variable.
     *
     * @return The type of the variable.
     */
    /**
     * Set the type of the variable.
     *
     * @param contentType The type of the variable
     */
    /** Type of the variable.  */
    public var contentType: WasmType? = null
    /**
     * Check if this varible is mutable.
     *
     * @return True if this variable is mutable and `false` otherwise.
     */
    /**
     * Set whether this variable is mutable.
     *
     * @param mutable True if this variable is mutable and `false` otherwise.
     */
    /** Whether the variable is mutable.  */
    public var isMutable: Boolean = false
}
