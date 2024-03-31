package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.LinkingSymbolType
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.visitors.LinkingSectionVisitor

public class LinkingSectionWriter(private val context: WriterContext) : LinkingSectionVisitor {
    private var numberOfLinks = 0u
    private val body = ByteBuffer()

    public override fun visitSymbol(index: UInt, symbolType: LinkingSymbolType, flags: UInt) {
        // TODO

        numberOfLinks++
    }

    public override fun visitSegment(name: String, alignment: UInt, flags: UInt) {
        // TODO

        numberOfLinks++
    }

    public override fun visitDataAlignment(alignment: Long) {
        // TODO

        numberOfLinks++
    }

    public override fun visitSectionSymbol(index: UInt, flags: UInt, sectionIndex: UInt) {
        // TODO

        numberOfLinks++
    }

    public override fun visitDataSymbol(index: UInt, flags: UInt, name: String, segmentIndex: UInt, offset: UInt, size: UInt) {
        // TODO

        numberOfLinks++
    }

    public override fun visitFunctionSymbol(index: UInt, flags: UInt, name: String, functionIndex: UInt) {
        // TODO

        numberOfLinks++
    }

    public override fun visitGlobalSymbol(index: UInt, flags: UInt, name: String, globalIndex: UInt) {
        // TODO

        numberOfLinks++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeString(SectionName.LINKING.sectionName)

        payload.writeVarUInt32(numberOfLinks)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.IMPORT, context.options.isCanonical, buffer.toByteArray())
    }
}
