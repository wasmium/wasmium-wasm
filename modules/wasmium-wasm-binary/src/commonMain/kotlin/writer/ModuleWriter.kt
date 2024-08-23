package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.visitor.CodeSectionVisitor
import org.wasmium.wasm.binary.visitor.DataCountSectionVisitor
import org.wasmium.wasm.binary.visitor.DataSectionVisitor
import org.wasmium.wasm.binary.visitor.ElementSectionVisitor
import org.wasmium.wasm.binary.visitor.ExportSectionVisitor
import org.wasmium.wasm.binary.visitor.ExternalDebugSectionVisitor
import org.wasmium.wasm.binary.visitor.FunctionSectionVisitor
import org.wasmium.wasm.binary.visitor.GlobalSectionVisitor
import org.wasmium.wasm.binary.visitor.ImportSectionVisitor
import org.wasmium.wasm.binary.visitor.LinkingSectionVisitor
import org.wasmium.wasm.binary.visitor.MemorySectionVisitor
import org.wasmium.wasm.binary.visitor.ModuleVisitor
import org.wasmium.wasm.binary.visitor.NameSectionVisitor
import org.wasmium.wasm.binary.visitor.RelocationSectionVisitor
import org.wasmium.wasm.binary.visitor.SourceMapSectionVisitor
import org.wasmium.wasm.binary.visitor.StartSectionVisitor
import org.wasmium.wasm.binary.visitor.TableSectionVisitor
import org.wasmium.wasm.binary.visitor.TagSectionVisitor
import org.wasmium.wasm.binary.visitor.TypeSectionVisitor
import org.wasmium.wasm.binary.visitor.UnknownSectionVisitor

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

    public override fun visitSourceMapSection(sourceMapUrl: String): SourceMapSectionVisitor {
        return SourceMapSectionWriter(context, sourceMapUrl)
    }

    public override fun visitExternalDebugSection(externalDebugUrl: String): ExternalDebugSectionVisitor {
        return ExternalDebugSectionWriter(context, externalDebugUrl)
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
