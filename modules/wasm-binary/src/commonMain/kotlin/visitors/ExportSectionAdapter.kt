package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ExternalKind

public open class ExportSectionAdapter(protected val delegate: ExportSectionVisitor? = null) : ExportSectionVisitor {
    public override fun visitExport(exportIndex: UInt, externalKind: ExternalKind, itemIndex: UInt, name: String) {
        delegate?.visitExport(exportIndex, externalKind, itemIndex, name)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
