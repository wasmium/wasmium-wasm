package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class ImportSectionWriter(private val context: WriterContext) : ImportSectionVisitor {
    private var numberOfImports = 0u
    private val body = ByteBuffer()

    public override fun visitFunction(moduleName: String, fieldName: String, typeIndex: UInt) {
        WasmBinaryWriter(body).writeString(moduleName)
        WasmBinaryWriter(body).writeString(fieldName)
        WasmBinaryWriter(body).writeExternalKind(ExternalKind.FUNCTION)
        WasmBinaryWriter(body).writeIndex(typeIndex)

        numberOfImports++
    }

    public override fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutable: Boolean) {
        WasmBinaryWriter(body).writeString(moduleName)
        WasmBinaryWriter(body).writeString(fieldName)
        WasmBinaryWriter(body).writeExternalKind(ExternalKind.GLOBAL)
        WasmBinaryWriter(body).writeType(type)
        WasmBinaryWriter(body).writeVarUInt1(if (mutable) 1u else 0u)

        numberOfImports++
    }

    public override fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: ResizableLimits) {
        WasmBinaryWriter(body).writeString(moduleName)
        WasmBinaryWriter(body).writeString(fieldName)
        WasmBinaryWriter(body).writeExternalKind(ExternalKind.TABLE)
        WasmBinaryWriter(body).writeType(elementType)
        WasmBinaryWriter(body).writeResizableLimits(limits)

        numberOfImports++
    }

    public override fun visitMemory(moduleName: String, fieldName: String, limits: ResizableLimits) {
        WasmBinaryWriter(body).writeString(moduleName)
        WasmBinaryWriter(body).writeString(fieldName)
        WasmBinaryWriter(body).writeExternalKind(ExternalKind.MEMORY)
        WasmBinaryWriter(body).writeResizableLimits(limits)

        numberOfImports++
    }

    public override fun visitException(moduleName: String, fieldName: String, exceptionTypes: List<WasmType>) {
        WasmBinaryWriter(body).writeString(moduleName)
        WasmBinaryWriter(body).writeString(fieldName)
        WasmBinaryWriter(body).writeExternalKind(ExternalKind.EXCEPTION)
        WasmBinaryWriter(body).writeVarUInt32(exceptionTypes.size.toUInt())

        for (wasmType in exceptionTypes) {
            WasmBinaryWriter(body).writeType(wasmType)
        }

        numberOfImports++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeVarUInt32(numberOfImports)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.IMPORT, context.options.isCanonical, buffer.toByteArray())
    }
}
