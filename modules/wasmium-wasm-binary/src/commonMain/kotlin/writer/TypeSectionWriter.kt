package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.FunctionType
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitor.TypeSectionVisitor

public class TypeSectionWriter(private val context: WriterContext) : TypeSectionVisitor {
    private var numberOfTypes: UInt = 0u
    private var body = ByteBuffer()
    private val writer = WasmBinaryWriter(body)

    public override fun visitType(functionType: FunctionType) {
        writer.writeFunctionType(functionType)

        context.functionTypes.add(functionType)

        numberOfTypes++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfTypes)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.TYPE, buffer.toByteArray())
    }
}
