package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.TypeSectionVisitor

public class TypeSectionWriter(private val context: WriterContext) : TypeSectionVisitor {
    private var numberOfTypes: UInt = 0u
    private var body = ByteBuffer()
    private val writer = WasmBinaryWriter(body)

    public override fun visitType(parameters: List<WasmType>, results: List<WasmType>) {
        writer.writeType(WasmType.FUNC)

        writer.writeVarUInt32(parameters.size.toUInt())
        for (parameter in parameters) {
            writer.writeType(parameter)
        }

        writer.writeVarUInt1(results.size.toUInt())
        for (result in results) {
            writer.writeType(result)
        }

        numberOfTypes++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfTypes)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.TYPE, context.options.isCanonical, buffer.toByteArray())
    }
}
