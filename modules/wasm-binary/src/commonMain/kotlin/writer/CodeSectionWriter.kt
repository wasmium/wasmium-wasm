package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.LocalVariable
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitor.CodeSectionVisitor
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class CodeSectionWriter(private val context: WriterContext) : CodeSectionVisitor {
    private var numberOfCodes = 0u
    private val codes = mutableListOf<ByteBuffer>()

    public override fun visitCode(locals: List<LocalVariable>): ExpressionVisitor {
        numberOfCodes++

        val buffer = ByteBuffer()
        val writer = WasmBinaryWriter(buffer)
        codes.add(buffer)

        writer.writeVarUInt32(locals.size.toUInt())
        for (local in locals) {
            writer.writeVarUInt32(local.count)
            writer.writeType(local.type)
        }

        return ExpressionWriter(context, buffer)
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfCodes)
        for (code in codes) {
            val bytes = code.toByteArray()

            payload.writeVarUInt32(bytes.size.toUInt())
            payload.writeByteArray(bytes)
        }

        context.writer.writeSection(SectionKind.CODE, buffer.toByteArray())
    }
}
