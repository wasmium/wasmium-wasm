package org.wasmium.wasm.binary.validator

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

public class ModuleValidator(private val delegate: ModuleVisitor? = null, private val options: ValidatorOptions) : ModuleVisitor {
    private val context = ValidatorContext(options)

    override fun visitHeader(version: UInt) {
        delegate?.visitHeader(version)
    }

    override fun visitTypeSection(): TypeSectionVisitor = TypeSectionValidator(delegate?.visitTypeSection(), context)

    override fun visitFunctionSection(): FunctionSectionVisitor = FunctionSectionValidator(delegate?.visitFunctionSection(), context)

    override fun visitStartSection(functionIndex: UInt): StartSectionVisitor = StartSectionValidator(delegate?.visitStartSection(functionIndex), context, functionIndex)

    override fun visitImportSection(): ImportSectionVisitor = ImportSectionValidator(delegate?.visitImportSection(), context)

    override fun visitExportSection(): ExportSectionVisitor = ExportSectionValidator(delegate?.visitExportSection(), context)

    override fun visitTableSection(): TableSectionVisitor = TableSectionValidator(delegate?.visitTableSection(), context)

    override fun visitElementSection(): ElementSectionVisitor = ElementSectionValidator(delegate?.visitElementSection(), context)

    override fun visitGlobalSection(): GlobalSectionVisitor = GlobalSectionValidator(delegate?.visitGlobalSection(), context)

    override fun visitCodeSection(): CodeSectionVisitor = CodeSectionValidator(delegate?.visitCodeSection(), context)

    override fun visitMemorySection(): MemorySectionVisitor = MemorySectionValidator(delegate?.visitMemorySection(), context)

    override fun visitDataSection(): DataSectionVisitor = DataSectionValidator(delegate?.visitDataSection(), context)

    override fun visitTagSection(): TagSectionVisitor = TagSectionValidator(delegate?.visitTagSection(), context)

    override fun visitRelocationSection(): RelocationSectionVisitor = RelocationSectionValidator(delegate?.visitRelocationSection(), context)

    override fun visitUnknownSection(name: String, content: ByteArray): UnknownSectionVisitor = UnknownSectionValidator(delegate?.visitUnknownSection(name, content), context)

    override fun visitLinkingSection(): LinkingSectionVisitor = LinkingSectionValidator(delegate?.visitLinkingSection(), context)

    override fun visitNameSection(): NameSectionVisitor = NameSectionValidator(delegate?.visitNameSection(), context)

    override fun visitDataCountSection(dataCount: UInt): DataCountSectionVisitor = DataCountSectionValidator(delegate?.visitDataCountSection(dataCount), context)

    override fun visitSourceMapSection(sourceMapUrl: String): SourceMapSectionVisitor = SourceMapSectionValidator(delegate?.visitSourceMapSection(sourceMapUrl), context)

    override fun visitExternalDebugSection(externalDebugUrl: String): ExternalDebugSectionVisitor =
        ExternalDebugSectionValidator(delegate?.visitExternalDebugSection(externalDebugUrl), context)

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
