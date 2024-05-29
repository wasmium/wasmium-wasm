package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.sections.RelocationType
import org.wasmium.wasm.binary.visitors.RelocationSectionVisitor

public class RelocationSectionValidator(private val delegate: RelocationSectionVisitor? = null, private val context: ValidatorContext) : RelocationSectionVisitor {
    override fun visitRelocation(sectionKind: SectionKind, sectionName: String?, relocationTypes: List<RelocationType>) {
        when (sectionKind) {
            SectionKind.TYPE,
            SectionKind.IMPORT,
            SectionKind.FUNCTION,
            SectionKind.TABLE,
            SectionKind.MEMORY,
            SectionKind.TAG,
            SectionKind.GLOBAL,
            SectionKind.EXPORT,
            SectionKind.START,
            SectionKind.ELEMENT,
            SectionKind.DATA_COUNT -> {
                throw ValidatorException("Relocation of $sectionKind is not allowed. Only code, data and custom sections are allowed")
            }

            else -> {
                // valid
            }
        }

        delegate?.visitRelocation(sectionKind, sectionName, relocationTypes)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
