package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.IndexName
import org.wasmium.wasm.binary.tree.NameKind
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.visitors.NameSectionVisitor

public class NameSectionWriter(
    private val context: WriterContext
) : NameSectionVisitor {
    private val body = ByteBuffer()

    public override fun visitModuleName(name: String) {
        val payload = WasmBinaryWriter(body)
        payload.writeNameKind(NameKind.MODULE)

        val buffer = ByteBuffer()
        WasmBinaryWriter(buffer).writeString(name)

        // write body size
        payload.writeVarUInt32(buffer.length.toUInt())
        // write body
        payload.writeByteArray(buffer.toByteArray())
    }

    public override fun visitLocalNames(functionIndex: UInt, names: List<IndexName>) {
        val payload = WasmBinaryWriter(body)
        payload.writeNameKind(NameKind.LOCAL)

        val buffer = ByteBuffer()
        WasmBinaryWriter(buffer).writeIndex(functionIndex)

        // write locals
        val localsBuffer = ByteBuffer()
        for (name in names) {
            WasmBinaryWriter(localsBuffer).writeIndex(name.index)
            WasmBinaryWriter(localsBuffer).writeString(name.name)
        }
        // write locals size
        WasmBinaryWriter(buffer).writeVarUInt32(localsBuffer.length.toUInt())
        // write locals body
        WasmBinaryWriter(buffer).writeByteArray(localsBuffer.toByteArray())

        // write body size
        payload.writeVarUInt32(buffer.length.toUInt())
        // write body
        payload.writeByteArray(buffer.toByteArray())
    }

    override fun visitLabelNames(functionIndex: UInt, names: List<IndexName>) {
        val payload = WasmBinaryWriter(body)
        payload.writeNameKind(NameKind.LABEL)

        val buffer = ByteBuffer()
        WasmBinaryWriter(buffer).writeIndex(functionIndex)

        // write locals
        val localsBuffer = ByteBuffer()
        for (name in names) {
            WasmBinaryWriter(localsBuffer).writeIndex(name.index)
            WasmBinaryWriter(localsBuffer).writeString(name.name)
        }
        // write locals size
        WasmBinaryWriter(buffer).writeVarUInt32(localsBuffer.length.toUInt())
        // write locals body
        WasmBinaryWriter(buffer).writeByteArray(localsBuffer.toByteArray())

        // write body size
        payload.writeVarUInt32(buffer.length.toUInt())
        // write body
        payload.writeByteArray(buffer.toByteArray())
    }

    override fun visitFieldNames(functionIndex: UInt, names: List<IndexName>) {
        val payload = WasmBinaryWriter(body)
        payload.writeNameKind(NameKind.FIELD)

        val buffer = ByteBuffer()
        WasmBinaryWriter(buffer).writeIndex(functionIndex)

        // write locals
        val localsBuffer = ByteBuffer()
        for (name in names) {
            WasmBinaryWriter(localsBuffer).writeIndex(name.index)
            WasmBinaryWriter(localsBuffer).writeString(name.name)
        }
        // write locals size
        WasmBinaryWriter(buffer).writeVarUInt32(localsBuffer.length.toUInt())
        // write locals body
        WasmBinaryWriter(buffer).writeByteArray(localsBuffer.toByteArray())

        // write body size
        payload.writeVarUInt32(buffer.length.toUInt())
        // write body
        payload.writeByteArray(buffer.toByteArray())
    }

    public override fun visitFunctionNames(names: List<IndexName>) {
        val payload = WasmBinaryWriter(body)
        payload.writeNameKind(NameKind.FUNCTION)

        val buffer = ByteBuffer()
        val writer = WasmBinaryWriter(buffer)
        writer.writeVarUInt32(names.size.toUInt())

        for (name in names) {
            writer.writeIndex(name.index)
            writer.writeString(name.name)
        }

        // write body size
        payload.writeVarUInt32(buffer.length.toUInt())
        // write body
        payload.writeByteArray(buffer.toByteArray())
    }

    override fun visitGlobalNames(names: List<IndexName>) {
        val payload = WasmBinaryWriter(body)
        payload.writeNameKind(NameKind.FUNCTION)

        val buffer = ByteBuffer()
        val writer = WasmBinaryWriter(buffer)
        writer.writeVarUInt32(names.size.toUInt())

        for (name in names) {
            writer.writeIndex(name.index)
            writer.writeString(name.name)
        }

        // write body size
        payload.writeVarUInt32(buffer.length.toUInt())
        // write body
        payload.writeByteArray(buffer.toByteArray())
    }

    override fun visitTagNames(names: List<IndexName>) {
        val payload = WasmBinaryWriter(body)
        payload.writeNameKind(NameKind.FUNCTION)

        val buffer = ByteBuffer()
        val writer = WasmBinaryWriter(buffer)
        writer.writeVarUInt32(names.size.toUInt())

        for (name in names) {
            writer.writeIndex(name.index)
            writer.writeString(name.name)
        }

        // write body size
        payload.writeVarUInt32(buffer.length.toUInt())
        // write body
        payload.writeByteArray(buffer.toByteArray())
    }

    override fun visitTableNames(names: List<IndexName>) {
        val payload = WasmBinaryWriter(body)
        payload.writeNameKind(NameKind.FUNCTION)

        val buffer = ByteBuffer()
        val writer = WasmBinaryWriter(buffer)
        writer.writeVarUInt32(names.size.toUInt())

        for (name in names) {
            writer.writeIndex(name.index)
            writer.writeString(name.name)
        }

        // write body size
        payload.writeVarUInt32(buffer.length.toUInt())
        // write body
        payload.writeByteArray(buffer.toByteArray())
    }

    override fun visitMemoryNames(names: List<IndexName>) {
        val payload = WasmBinaryWriter(body)
        payload.writeNameKind(NameKind.FUNCTION)

        val buffer = ByteBuffer()
        val writer = WasmBinaryWriter(buffer)
        writer.writeVarUInt32(names.size.toUInt())

        for (name in names) {
            writer.writeIndex(name.index)
            writer.writeString(name.name)
        }

        // write body size
        payload.writeVarUInt32(buffer.length.toUInt())
        // write body
        payload.writeByteArray(buffer.toByteArray())
    }

    override fun visitElementNames(names: List<IndexName>) {
        val payload = WasmBinaryWriter(body)
        payload.writeNameKind(NameKind.FUNCTION)

        val buffer = ByteBuffer()
        val writer = WasmBinaryWriter(buffer)
        writer.writeVarUInt32(names.size.toUInt())

        for (name in names) {
            writer.writeIndex(name.index)
            writer.writeString(name.name)
        }

        // write body size
        payload.writeVarUInt32(buffer.length.toUInt())
        // write body
        payload.writeByteArray(buffer.toByteArray())
    }

    override fun visitDataNames(names: List<IndexName>) {
        val payload = WasmBinaryWriter(body)
        payload.writeNameKind(NameKind.FUNCTION)

        val buffer = ByteBuffer()
        val writer = WasmBinaryWriter(buffer)
        writer.writeVarUInt32(names.size.toUInt())

        for (name in names) {
            writer.writeIndex(name.index)
            writer.writeString(name.name)
        }

        // write body size
        payload.writeVarUInt32(buffer.length.toUInt())
        // write body
        payload.writeByteArray(buffer.toByteArray())
    }

    override fun visitTypeNames(names: List<IndexName>) {
        val payload = WasmBinaryWriter(body)
        payload.writeNameKind(NameKind.FUNCTION)

        val buffer = ByteBuffer()
        val writer = WasmBinaryWriter(buffer)
        writer.writeVarUInt32(names.size.toUInt())

        for (name in names) {
            writer.writeIndex(name.index)
            writer.writeString(name.name)
        }

        // write body size
        payload.writeVarUInt32(buffer.length.toUInt())
        // write body
        payload.writeByteArray(buffer.toByteArray())
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeString(SectionName.NAME.sectionName)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.CUSTOM, buffer.toByteArray())
    }
}
