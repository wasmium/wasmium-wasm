package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.MemoryLimits
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.TableSectionVisitor

public class TableSectionWriter(private val context: WriterContext) : TableSectionVisitor {
    private var numberOfTables: UInt = 0u
    private var body = ByteBuffer()
    private val writer = WasmBinaryWriter(body)

    public override fun visitTable(elementType: WasmType, limits: MemoryLimits) {
        writer.writeType(elementType)
        writer.writeMemoryLimits(limits)

        numberOfTables++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfTables)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.TABLE, buffer.toByteArray())
    }
}
