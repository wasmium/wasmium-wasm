package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind

public class ExceptionImportNode : ImportNode() {
    public var exceptionIndex: UInt? = null
    public var exceptionType: ExceptionTypeNode? = null

    public override val externalKind: ExternalKind
        get() = ExternalKind.EXCEPTION

}
