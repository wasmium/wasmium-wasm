package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.RelocationSectionVisitor

public class RelocationSectionValidator(private val delegate: RelocationSectionVisitor, private val context: ValidatorContext) : RelocationSectionVisitor {
    override fun visitRelocation(relocationKind: RelocationKind, offset: UInt, index: UInt, addend: Int?) {
        delegate.visitRelocation(relocationKind, offset, index, addend)
    }

    override fun visitSection(sectionKind: SectionKind, sectionName: String) {
        delegate.visitSection(sectionKind, sectionName)
    }

    override fun visitEnd() {
        delegate.visitEnd()
    }
}
