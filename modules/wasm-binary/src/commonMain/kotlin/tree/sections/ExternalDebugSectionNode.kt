package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.visitors.ExternalDebugSectionVisitor

public class ExternalDebugSectionNode(
    public val url: String,
) : CustomSectionNode(SectionName.EXTERNAL_DEBUG_INFO.sectionName), ExternalDebugSectionVisitor {

    public fun accept(externalDebugSectionVisitor: ExternalDebugSectionVisitor) {
        externalDebugSectionVisitor.visitEnd()
    }

    override fun visitEnd() {
        // empty
    }
}
