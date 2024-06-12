package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.LinkingSymbolType
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.visitor.LinkingSectionVisitor

public class LinkingSectionWriter(private val context: WriterContext) : LinkingSectionVisitor {
    private var numberOfLinks = 0u
    private val body = ByteBuffer()
    private val writer = WasmBinaryWriter(body)

    public override fun visitSymbol(symbolType: LinkingSymbolType, flags: UInt) {
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

    public override fun visitSectionSymbol(flags: UInt, sectionIndex: UInt) {
        // TODO

        numberOfLinks++
    }

    public override fun visitDataSymbol(flags: UInt, name: String, segmentIndex: UInt, offset: UInt, size: UInt) {
        // TODO

        numberOfLinks++
    }

    public override fun visitFunctionSymbol(flags: UInt, name: String, functionIndex: UInt) {
        // TODO

        numberOfLinks++
    }

    public override fun visitGlobalSymbol(flags: UInt, name: String, globalIndex: UInt) {
        // TODO

        numberOfLinks++
    }

    public override fun visitEnd() {
        val buffer = ByteBuffer()
        val payload = WasmBinaryWriter(buffer)

        payload.writeString(SectionName.LINKING.sectionName)

        payload.writeVarUInt32(numberOfLinks)
        payload.writeByteArray(body.toByteArray())

        context.writer.writeSection(SectionKind.CUSTOM, buffer.toByteArray())
    }
}
