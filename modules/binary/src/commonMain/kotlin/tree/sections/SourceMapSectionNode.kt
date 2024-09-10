package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.visitor.SourceMapSectionVisitor

public class SourceMapSectionNode(
    public val url: String,
) : CustomSectionNode(SectionName.SOURCE_MAPPING_URL.sectionName), SourceMapSectionVisitor {

    public fun accept(sourceMapSectionVisitor: SourceMapSectionVisitor) {
        sourceMapSectionVisitor.visitEnd()
    }

    override fun visitEnd() {
        // empty
    }
}
