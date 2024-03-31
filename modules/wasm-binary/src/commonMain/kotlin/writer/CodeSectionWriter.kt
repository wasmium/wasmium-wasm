package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.LocalVariable
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.CodeSectionVisitor
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class CodeSectionWriter(private val context: WriterContext) : CodeSectionVisitor {
    private var numberOfCodes = 0u
    private val codes = mutableListOf<ByteBuffer>()

    public override fun visitCode(locals: List<LocalVariable>): ExpressionVisitor {
        numberOfCodes++

        val buffer = ByteBuffer()
        codes.add(buffer)

        WasmBinaryWriter(buffer).writeVarUInt32(locals.size.toUInt())
        for (local in locals) {
            WasmBinaryWriter(buffer).writeVarUInt32(local.count)
            WasmBinaryWriter(buffer).writeType(local.type)
        }

        val expressionWriter = ExpressionWriter(context, buffer)
        return expressionWriter
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfCodes)
        for (code in codes) {
            val bytes = code.toByteArray()

            payload.writeVarUInt32(bytes.size.toUInt(), context.options.isCanonical)
            payload.writeByteArray(bytes)
        }

        context.writer.writeSection(SectionKind.CODE, context.options.isCanonical, buffer.toByteArray())
    }
}
