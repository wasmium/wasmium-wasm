package org.wasmium.wasm.binary.validator

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

    override fun visitSourceMapSection(sourceMap: String): SourceMapSectionVisitor = SourceMapSectionValidator(delegate?.visitSourceMapSection(sourceMap), context)

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
