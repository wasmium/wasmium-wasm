package org.wasmium.wasm.binary.visitors

public open class ModuleAdapter(protected val delegate: ModuleVisitor? = null) : ModuleVisitor {

    override fun visitHeader(version: UInt): Unit = delegate?.visitHeader(version) ?: Unit

    override fun visitTypeSection(): TypeSectionVisitor = TypeSectionAdapter(delegate?.visitTypeSection())

    override fun visitFunctionSection(): FunctionSectionVisitor = FunctionSectionAdapter(delegate?.visitFunctionSection())

    override fun visitStartSection(functionIndex: UInt): StartSectionVisitor = StartSectionAdapter(delegate?.visitStartSection(functionIndex))

    override fun visitImportSection(): ImportSectionVisitor = ImportSectionAdapter(delegate?.visitImportSection())

    override fun visitExportSection(): ExportSectionVisitor = ExportSectionAdapter(delegate?.visitExportSection())

    override fun visitTableSection(): TableSectionVisitor = TableSectionAdapter(delegate?.visitTableSection())

    override fun visitElementSection(): ElementSectionVisitor = ElementSectionAdapter(delegate?.visitElementSection())

    override fun visitGlobalSection(): GlobalSectionVisitor = GlobalSectionAdapter(delegate?.visitGlobalSection())

    override fun visitCodeSection(): CodeSectionVisitor = CodeSectionAdapter(delegate?.visitCodeSection())

    override fun visitMemorySection(): MemorySectionVisitor = MemorySectionAdapter(delegate?.visitMemorySection())

    override fun visitDataSection(): DataSectionVisitor = DataSectionAdapter(delegate?.visitDataSection())

    override fun visitExceptionSection(): ExceptionSectionVisitor = ExceptionSectionAdapter(delegate?.visitExceptionSection())

    override fun visitRelocationSection(): RelocationSectionVisitor = RelocationSectionAdapter(delegate?.visitRelocationSection())

    override fun visitUnknownSection(name: String, content: ByteArray): UnknownSectionVisitor = UnknownSectionAdapter(delegate?.visitUnknownSection(name, content))

    override fun visitLinkingSection(): LinkingSectionVisitor = LinkingSectionAdapter(delegate?.visitLinkingSection())

    override fun visitNameSection(): NameSectionVisitor = NameSectionAdapter(delegate?.visitNameSection())

    override fun visitDataCountSection(dataCount: UInt): DataCountSectionVisitor = DataCountSectionAdapter(delegate?.visitDataCountSection(dataCount))

    override fun visitSourceMapSection(sourceMap: String): SourceMapSectionVisitor = SourceMapSectionAdapter(delegate?.visitSourceMapSection(sourceMap))

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
