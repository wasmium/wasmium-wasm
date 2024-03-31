package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.visitors.RelocationSectionVisitor

public class RelocationSectionWriter(private val context: WriterContext) : RelocationSectionVisitor {
    private var numberOfRelocations = 0u
    private val body = ByteBuffer()

    public override fun visitSection(sectionKind: SectionKind, sectionName: String) {
        // TODO
    }

    public override fun visitRelocation(relocationKind: RelocationKind, offset: UInt, index: UInt, addend: Int?) {
        // TODO
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeString(SectionName.RELOCATION.sectionName)

        payload.writeVarUInt32(numberOfRelocations)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.CUSTOM, context.options.isCanonical, buffer.toByteArray())
    }
}
