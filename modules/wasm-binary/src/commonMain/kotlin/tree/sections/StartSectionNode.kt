package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.StartSectionVisitor

public class StartSectionNode(
    public val functionIndex: UInt
) : SectionNode(SectionKind.START), StartSectionVisitor {

    public fun accept(startSectionVisitor: StartSectionVisitor) {
        startSectionVisitor.visitEnd()
    }

    public override fun visitEnd() {
        // empty
    }
}
