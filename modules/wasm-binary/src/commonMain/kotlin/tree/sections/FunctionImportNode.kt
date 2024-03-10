package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind


public class FunctionImportNode : ImportNode() {
    public var functionIndex: UInt? = null
    public var typeIndex: UInt? = null

    public override val externalKind: ExternalKind
        get() = ExternalKind.FUNCTION

}
