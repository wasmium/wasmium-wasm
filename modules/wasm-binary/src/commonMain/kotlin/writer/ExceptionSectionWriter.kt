package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExceptionSectionVisitor

public class ExceptionSectionWriter(private val context: WriterContext) : ExceptionSectionVisitor {
    private var numberOfExceptions = 0u
    private val body = ByteBuffer()

    public override fun visitExceptionType(types: List<WasmType>) {
        WasmBinaryWriter(body).writeVarUInt32(types.size.toUInt())
        for (type in types) {
            WasmBinaryWriter(body).writeType(type)
        }

        numberOfExceptions++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeString(SectionName.EXCEPTION.sectionName)

        payload.writeVarUInt32(numberOfExceptions)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.CUSTOM, context.options.isCanonical, buffer.toByteArray())
    }
}
