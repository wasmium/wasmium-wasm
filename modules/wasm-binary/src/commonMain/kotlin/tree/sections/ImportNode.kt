package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind


public abstract class ImportNode {
    public var importIndex: UInt? = null

    /** The module name.  */
    public var module: String? = null

    /** The field value.  */
    public var field: String? = null

    /** The type of the import.  */
    public abstract val externalKind: ExternalKind
        get

}
