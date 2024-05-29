package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.visitors.CodeSectionVisitor
import org.wasmium.wasm.binary.visitors.DataCountSectionVisitor
import org.wasmium.wasm.binary.visitors.DataSectionVisitor
import org.wasmium.wasm.binary.visitors.ElementSectionVisitor
import org.wasmium.wasm.binary.visitors.ExportSectionVisitor
import org.wasmium.wasm.binary.visitors.FunctionSectionVisitor
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor
import org.wasmium.wasm.binary.visitors.LinkingSectionVisitor
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor
import org.wasmium.wasm.binary.visitors.ModuleVisitor
import org.wasmium.wasm.binary.visitors.NameSectionVisitor
import org.wasmium.wasm.binary.visitors.RelocationSectionVisitor
import org.wasmium.wasm.binary.visitors.SourceMapSectionVisitor
import org.wasmium.wasm.binary.visitors.StartSectionVisitor
import org.wasmium.wasm.binary.visitors.TableSectionVisitor
import org.wasmium.wasm.binary.visitors.TagSectionVisitor
import org.wasmium.wasm.binary.visitors.TypeSectionVisitor
import org.wasmium.wasm.binary.visitors.UnknownSectionVisitor

public class ModuleWriter(
    private var context: WriterContext,
) : ModuleVisitor {

    public override fun visitHeader(version: UInt) {
        context.writer.writeUInt32(WasmBinary.Meta.MAGIC_NUMBER)
        context.writer.writeUInt32(version)
    }

    public override fun visitTypeSection(): TypeSectionVisitor {
        return TypeSectionWriter(context)
    }

    public override fun visitFunctionSection(): FunctionSectionVisitor {
        return FunctionSectionWriter(context)
    }

    public override fun visitStartSection(functionIndex: UInt): StartSectionVisitor {
        return StartSectionWriter(context, functionIndex)
    }

    public override fun visitImportSection(): ImportSectionVisitor {
        return ImportSectionWriter(context)
    }

    public override fun visitExportSection(): ExportSectionVisitor {
        return ExportSectionWriter(context)
    }

    public override fun visitTableSection(): TableSectionVisitor {
        return TableSectionWriter(context)
    }

    public override fun visitElementSection(): ElementSectionVisitor {
        return ElementSectionWriter(context)
    }

    public override fun visitGlobalSection(): GlobalSectionVisitor {
        return GlobalSectionWriter(context)
    }

    public override fun visitCodeSection(): CodeSectionVisitor {
        return CodeSectionWriter(context)
    }

    public override fun visitMemorySection(): MemorySectionVisitor {
        return MemorySectionWriter(context)
    }

    public override fun visitDataSection(): DataSectionVisitor {
        return DataSectionWriter(context)
    }

    public override fun visitRelocationSection(): RelocationSectionVisitor {
        return RelocationSectionWriter(context)
    }

    public override fun visitLinkingSection(): LinkingSectionVisitor {
        return LinkingSectionWriter(context)
    }

    public override fun visitNameSection(): NameSectionVisitor {
        return NameSectionWriter(context)
    }

    override fun visitDataCountSection(dataCount: UInt): DataCountSectionVisitor {
        return DataCountSectionWriter(context, dataCount)
    }

    public override fun visitUnknownSection(name: String, content: ByteArray): UnknownSectionVisitor {
        return UnknownSectionWriter(context, name, content)
    }

    public override fun visitSourceMapSection(sourceMap: String): SourceMapSectionVisitor {
        return SourceMapSectionWriter(context, sourceMap)
    }

    override fun visitTagSection(): TagSectionVisitor {
        return TagSectionWriter(context)
    }

    public override fun visitEnd() {
        // empty
    }

    private fun writeLinkingSection(output: WasmBinaryWriter) {
//        val linkingSection = context.linkingSection
//        if (linkingSection != null) {
//            val payload: WasmSink = WasmSink()
//
//            // symbol info
//            writeLinkingSymbolTable(payload)
//
//            // segment table
//            writeLinkingSegmentInfo(payload)
//
//            writeSection(output, SectionKind.CUSTOM, Wasm.SECTION_NAME_LINKING, payload.getData())
//        }
    }

    private fun writeLinkingSegmentInfo(output: WasmBinaryWriter) {
//        val payload: WasmSink = WasmSink()
//
//        payload.writeVarUInt(context.getLinkingSection().getSegments().size())
//        for (segment in context.getLinkingSection().getSegments()) {
//            payload.writeString(segment.getName())
//            payload.writeUInt(segment.getAlignment())
//            payload.writeUInt(segment.getFlags())
//        }
//
//        output.writeLinkingKind(LinkingKind.SEGMENT_INFO)
//        // write payload size
//        output.writeVarUInt(payload.getLength(), context.options.isCanonical())
//        // write payload
//        output.write(payload)
    }

    private fun writeLinkingSymbolTable(payload: WasmBinaryWriter) {
//        // TODO
//        val subpayload: WasmSink = WasmSink()
//        subpayload.writeVarUInt(context.linkingSection?.symbolInfos?.size)
//        for (symbolInfo in context.linkingSection?.symbolInfos) {
//            subpayload.writeString(symbolInfo.getName())
//            subpayload.writeUInt(symbolInfo.getFlags())
//        }
//        payload.writeLinkingKind(LinkingKind.SYMBOL_TABLE)
//        // write subpayload size
//        payload.writeVarUInt(subpayload.getLength(), context.options.isCanonical())
//        // write subpayload
//        payload.write(subpayload)
    }
}
