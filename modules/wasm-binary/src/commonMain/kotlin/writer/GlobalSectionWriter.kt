package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExpressionVisitor
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor

public class GlobalSectionWriter(private val context: WriterContext) : GlobalSectionVisitor {
    private var numberOfGlobalVariables: UInt = 0u
    private var body = ByteBuffer()
    private val writer = WasmBinaryWriter(body)

    public override fun visitGlobalVariable(type: WasmType, mutable: Boolean): ExpressionVisitor {
        numberOfGlobalVariables++

        writer.writeType(type)

        val value = if (mutable) 1u else 0u
        writer.writeVarUInt1(value)

        return ExpressionWriter(context, body)
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfGlobalVariables)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.GLOBAL, context.options.isCanonical, buffer.toByteArray())
    }
}
