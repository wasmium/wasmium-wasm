package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ExternalKind

public open class ExportSectionAdapter(protected val delegate: ExportSectionVisitor? = null) : ExportSectionVisitor {
    public override fun visitExport(name: String, externalKind: ExternalKind, itemIndex: UInt) {
        delegate?.visitExport(name, externalKind, itemIndex)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
