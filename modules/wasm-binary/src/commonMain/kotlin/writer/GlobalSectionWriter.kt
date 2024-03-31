package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class GlobalSectionWriter(private val context: WriterContext) : GlobalSectionVisitor {
    private var numberOfGlobalVariables: UInt = 0u
    private var body = ByteBuffer()

    public override fun visitGlobalVariable(type: WasmType, mutable: Boolean): InitializerExpressionVisitor {
        numberOfGlobalVariables++

        WasmBinaryWriter(body).writeType(type)
        WasmBinaryWriter(body).writeVarUInt1(if (mutable) 1u else 0u)

        return InitializerExpressionWriter(context, body)
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfGlobalVariables)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.GLOBAL, context.options.isCanonical, buffer.toByteArray())
    }
}
