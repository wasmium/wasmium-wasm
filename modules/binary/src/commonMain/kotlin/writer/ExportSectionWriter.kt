package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitor.ExportSectionVisitor

public class ExportSectionWriter(private val context: WriterContext) : ExportSectionVisitor {
    private var numberOfExports = 0u
    private val body = ByteBuffer()
    private val writer = WasmBinaryWriter(body)

    public override fun visitExport(name: String, externalKind: ExternalKind, itemIndex: UInt) {
        writer.writeString(name)
        writer.writeExternalKind(externalKind)
        writer.writeIndex(itemIndex)

        numberOfExports++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfExports)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.EXPORT, buffer.toByteArray())
    }
}
