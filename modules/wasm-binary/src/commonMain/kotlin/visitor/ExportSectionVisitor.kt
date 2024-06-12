package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.ExternalKind

public interface ExportSectionVisitor {

    public fun visitExport(name: String, externalKind: ExternalKind, itemIndex: UInt)

    public fun visitEnd()
}
