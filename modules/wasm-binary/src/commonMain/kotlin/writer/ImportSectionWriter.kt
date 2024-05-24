package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.GlobalType.*
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class ImportSectionWriter(private val context: WriterContext) : ImportSectionVisitor {
    private var numberOfImports = 0u
    private val body = ByteBuffer()
    private val writer = WasmBinaryWriter(body)

    public override fun visitFunction(moduleName: String, fieldName: String, typeIndex: UInt) {
        writer.writeString(moduleName)
        writer.writeString(fieldName)
        writer.writeExternalKind(ExternalKind.FUNCTION)
        writer.writeIndex(typeIndex)

        numberOfImports++
    }

    public override fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutability: Mutability) {
        writer.writeString(moduleName)
        writer.writeString(fieldName)
        writer.writeExternalKind(ExternalKind.GLOBAL)
        writer.writeType(type)
        writer.writeMutability(mutability)

        numberOfImports++
    }

    public override fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: ResizableLimits) {
        writer.writeString(moduleName)
        writer.writeString(fieldName)
        writer.writeExternalKind(ExternalKind.TABLE)
        writer.writeType(elementType)
        writer.writeResizableLimits(limits)

        numberOfImports++
    }

    public override fun visitMemory(moduleName: String, fieldName: String, limits: ResizableLimits) {
        writer.writeString(moduleName)
        writer.writeString(fieldName)
        writer.writeExternalKind(ExternalKind.MEMORY)
        writer.writeResizableLimits(limits)

        numberOfImports++
    }

    public override fun visitTag(moduleName: String, fieldName: String, tagType: TagType) {
        writer.writeString(moduleName)
        writer.writeString(fieldName)
        writer.writeExternalKind(ExternalKind.TAG)
        writer.writeTagType(tagType)

        numberOfImports++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfImports)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.IMPORT, buffer.toByteArray())
    }
}
