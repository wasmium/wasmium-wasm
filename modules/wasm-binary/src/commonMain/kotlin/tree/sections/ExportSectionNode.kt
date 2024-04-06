package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.ExportSectionVisitor

public class ExportSectionNode : SectionNode(SectionKind.EXPORT), ExportSectionVisitor {
    public val exports: MutableList<ExportType> = mutableListOf()

    public fun accept(exportSectionVisitor: ExportSectionVisitor) {
        for (exportType in exports) {
            exportSectionVisitor.visitExport(exportType.name, exportType.kind, exportType.index)
        }

        exportSectionVisitor.visitEnd()
    }

    public override fun visitExport(name: String, externalKind: ExternalKind, itemIndex: UInt) {
        exports.add(ExportType(name, externalKind, itemIndex))
    }

    public override fun visitEnd() {
        // empty
    }
}
