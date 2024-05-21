package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.ElementSectionVisitor
import org.wasmium.wasm.binary.visitors.ElementSegmentVisitor

public class ElementSectionWriter(private val context: WriterContext) : ElementSectionVisitor {
    private var numberOfElements = 0u
    private val body = ByteBuffer()

    public override fun visitElementSegment(): ElementSegmentVisitor {
        numberOfElements++

        return ElementSegmentWriter(context, body)
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfElements)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.ELEMENT, buffer.toByteArray())
    }
}
