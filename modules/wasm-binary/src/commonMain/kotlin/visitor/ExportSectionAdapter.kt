package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.ExternalKind

public open class ExportSectionAdapter(protected val delegate: ExportSectionVisitor? = null) : ExportSectionVisitor {

    override fun visitExport(name: String, externalKind: ExternalKind, itemIndex: UInt): Unit = delegate?.visitExport(name, externalKind, itemIndex) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
