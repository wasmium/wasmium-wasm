package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ExternalKind

public interface ExportSectionVisitor {
    public fun visitExport(exportIndex: UInt, externalKind: ExternalKind, itemIndex: UInt, name: String)

    public fun visitEnd()
}
