package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
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
import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.tree.sections.RelocationType
import org.wasmium.wasm.binary.visitor.RelocationSectionVisitor

public class RelocationSectionWriter(private val context: WriterContext) : RelocationSectionVisitor {
    private var numberOfRelocations = 0u
    private val body = ByteBuffer()
    private val writer = WasmBinaryWriter(body)

    public override fun visitRelocation(sectionKind: SectionKind, sectionName: String?, relocationTypes: List<RelocationType>) {
        writer.writeSectionKind(sectionKind)

        if (sectionKind == SectionKind.CUSTOM) {
            if (sectionName == null) {
                throw IllegalStateException("Relocation of Custom Section must have a name")
            }

            writer.writeString(sectionName)
        }

        for (relocationType in relocationTypes) {
            writer.writeRelocationKind(relocationType.relocationKind)

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
                    writer.writeIndex(relocationType.offset)
                    writer.writeIndex(relocationType.index)
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
                    writer.writeIndex(relocationType.offset)
                    writer.writeIndex(relocationType.index)

                    if (relocationType.addend == null) {
                        throw IllegalStateException("Missing addend with writing relocation kind ${relocationType.relocationKind}")
                    }

                    writer.writeVarInt32(relocationType.addend!!)
                }
            }
        }

        numberOfRelocations++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeString(SectionName.RELOCATION.sectionName)

        payload.writeVarUInt32(numberOfRelocations)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.CUSTOM, buffer.toByteArray())
    }
}
