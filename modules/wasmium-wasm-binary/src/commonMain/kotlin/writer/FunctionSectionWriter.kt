package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.FunctionType
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitor.FunctionSectionVisitor

public class FunctionSectionWriter(private val context: WriterContext) : FunctionSectionVisitor {
    private var numberOfFunctions = 0u
    private val body = ByteBuffer()
    private val writer = WasmBinaryWriter(body)

    public override fun visitFunction(functionType: FunctionType) {
        val typeIndex = context.functionTypes.indexOf(functionType)

        writer.writeIndex(typeIndex.toUInt())

        numberOfFunctions++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfFunctions)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.FUNCTION, buffer.toByteArray())
    }
}
