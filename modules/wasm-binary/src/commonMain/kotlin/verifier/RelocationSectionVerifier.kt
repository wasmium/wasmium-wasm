package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.RelocationSectionVisitor

public class RelocationSectionVerifier(private val delegate: RelocationSectionVisitor? = null, private val context: VerifierContext) : RelocationSectionVisitor {
    private var done: Boolean = false
    private var numberOfRelocations: UInt = 0u

    override fun visitRelocation(relocationKind: RelocationKind, offset: UInt, index: UInt, addend: Int?) {
        checkEnd()

        numberOfRelocations++

        delegate?.visitRelocation(relocationKind, offset, index, addend)
    }

    override fun visitSection(sectionKind: SectionKind, sectionName: String) {
        checkEnd()

        delegate?.visitSection(sectionKind, sectionName)
    }

    override fun visitEnd() {
        checkEnd()

        if (this.numberOfRelocations > WasmBinary.MAX_RELOCATIONS) {
            throw VerifierException("Number of relocations $numberOfRelocations exceed the maximum of ${WasmBinary.MAX_RELOCATIONS}")
        }

        context.numberOfRelocations = numberOfRelocations

        done = true
        delegate?.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
