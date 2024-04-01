package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.ExternalKind

public open class ExportSectionAdapter(protected val delegate: ExportSectionVisitor? = null) : ExportSectionVisitor {

    public override fun visitExport(name: String, externalKind: ExternalKind, itemIndex: UInt): Unit = delegate?.visitExport(name, externalKind, itemIndex) ?: Unit

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
