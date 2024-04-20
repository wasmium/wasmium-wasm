package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.visitors.ExportSectionVisitor

public class ExportSectionValidator(private val delegate: ExportSectionVisitor? = null, private val context: ValidatorContext) : ExportSectionVisitor {
    private val names = mutableSetOf<String>()

    override fun visitExport(name: String, externalKind: ExternalKind, itemIndex: UInt) {
        if (!names.add(name)) {
            throw ParserException("Duplicate export name: $name")
        }

        delegate?.visitExport(name, externalKind, itemIndex)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
