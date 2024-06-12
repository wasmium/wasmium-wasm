package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.visitor.TagSectionVisitor

public class TagSectionWriter(private val context: WriterContext) : TagSectionVisitor {
    private var numberOfTags = 0u
    private val body = ByteBuffer()
    private val writer = WasmBinaryWriter(body)

    public override fun visitTag(tagType: TagType) {
        writer.writeTagType(tagType)

        numberOfTags++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfTags)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.TYPE, buffer.toByteArray())
    }
}
