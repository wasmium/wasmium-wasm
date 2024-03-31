package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.TypeSectionVisitor

public class TypeSectionWriter(private val context: WriterContext) : TypeSectionVisitor {
    private var numberOfSignatures: UInt = 0u
    private var body = ByteBuffer()

    public override fun visitType(parameters: List<WasmType>, results: List<WasmType>) {
        WasmBinaryWriter(body).writeType(WasmType.FUNC)

        WasmBinaryWriter(body).writeVarUInt32(parameters.size.toUInt())
        for (parameter in parameters) {
            WasmBinaryWriter(body).writeType(parameter)
        }

        WasmBinaryWriter(body).writeVarUInt1(results.size.toUInt())
        for (result in results) {
            WasmBinaryWriter(body).writeType(result)
        }

        numberOfSignatures++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfSignatures)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.TYPE, context.options.isCanonical, buffer.toByteArray())
    }
}
