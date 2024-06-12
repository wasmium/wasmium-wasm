package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.RelocationKind.EVENT_INDEX_LEB
import org.wasmium.wasm.binary.tree.RelocationKind.FUNCTION_INDEX_I32
import org.wasmium.wasm.binary.tree.RelocationKind.FUNCTION_INDEX_LEB
import org.wasmium.wasm.binary.tree.RelocationKind.FUNCTION_OFFSET_I32
import org.wasmium.wasm.binary.tree.RelocationKind.FUNCTION_OFFSET_I64
import org.wasmium.wasm.binary.tree.RelocationKind.GLOBAL_INDEX_I32
import org.wasmium.wasm.binary.tree.RelocationKind.GLOBAL_INDEX_LEB
import org.wasmium.wasm.binary.tree.RelocationKind.MEMORY_ADDRESS_I32
import org.wasmium.wasm.binary.tree.RelocationKind.MEMORY_ADDRESS_I64
import org.wasmium.wasm.binary.tree.RelocationKind.MEMORY_ADDRESS_LEB
import org.wasmium.wasm.binary.tree.RelocationKind.MEMORY_ADDRESS_LEB64
import org.wasmium.wasm.binary.tree.RelocationKind.MEMORY_ADDRESS_LOCREL_I32
import org.wasmium.wasm.binary.tree.RelocationKind.MEMORY_ADDRESS_SLEB
import org.wasmium.wasm.binary.tree.RelocationKind.MEMORY_ADDRESS_SLEB64
import org.wasmium.wasm.binary.tree.RelocationKind.SECTION_OFFSET_I32
import org.wasmium.wasm.binary.tree.RelocationKind.TABLE_INDEX_I32
import org.wasmium.wasm.binary.tree.RelocationKind.TABLE_INDEX_I64
import org.wasmium.wasm.binary.tree.RelocationKind.TABLE_INDEX_REL_SLEB64
import org.wasmium.wasm.binary.tree.RelocationKind.TABLE_INDEX_SLEB
import org.wasmium.wasm.binary.tree.RelocationKind.TABLE_INDEX_SLEB64
import org.wasmium.wasm.binary.tree.RelocationKind.TABLE_NUMBER_LEB
import org.wasmium.wasm.binary.tree.RelocationKind.TYPE_INDEX_LEB
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.sections.RelocationType
import org.wasmium.wasm.binary.visitor.RelocationSectionVisitor

public class RelocationSectionVerifier(private val delegate: RelocationSectionVisitor? = null, private val context: VerifierContext) : RelocationSectionVisitor {
    private var done: Boolean = false
    private var numberOfRelocations: UInt = 0u

    override fun visitRelocation(sectionKind: SectionKind, sectionName: String?, relocationTypes: List<RelocationType>) {
        checkEnd()

        for (relocationType in relocationTypes) {
            when (relocationType.relocationKind) {
                FUNCTION_INDEX_LEB,
                TABLE_INDEX_SLEB,
                TABLE_INDEX_I32,
                TYPE_INDEX_LEB,
                GLOBAL_INDEX_LEB,
                EVENT_INDEX_LEB,
                GLOBAL_INDEX_I32,
                TABLE_INDEX_SLEB64,
                TABLE_INDEX_I64,
                TABLE_NUMBER_LEB,
                TABLE_INDEX_REL_SLEB64,
                FUNCTION_INDEX_I32 -> {
                    if (relocationType.addend != null) {
                        throw VerifierException("Relocation of ${relocationType.relocationKind} must not have addend")
                    }
                }

                MEMORY_ADDRESS_LEB,
                MEMORY_ADDRESS_LEB64,
                MEMORY_ADDRESS_SLEB,
                MEMORY_ADDRESS_SLEB64,
                MEMORY_ADDRESS_I32,
                MEMORY_ADDRESS_I64,
                MEMORY_ADDRESS_LOCREL_I32,
                FUNCTION_OFFSET_I32,
                FUNCTION_OFFSET_I64,
                SECTION_OFFSET_I32 -> {
                    if (relocationType.addend == null) {
                        throw VerifierException("Relocation of ${relocationType.relocationKind} is missing addend")
                    }
                }
            }
        }

        numberOfRelocations++

        delegate?.visitRelocation(sectionKind, sectionName, relocationTypes)
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
