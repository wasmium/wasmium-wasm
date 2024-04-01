package org.wasmium.wasm.binary.visitors

public open class ModuleAdapter(protected val delegate: ModuleVisitor? = null) : ModuleVisitor {

    override fun visitHeader(version: UInt): Unit = delegate?.visitHeader(version) ?: Unit

    override fun visitTypeSection(): TypeSectionVisitor = delegate?.visitTypeSection() ?: TypeSectionAdapter()

    override fun visitFunctionSection(): FunctionSectionVisitor = delegate?.visitFunctionSection() ?: FunctionSectionAdapter()

    override fun visitStartSection(functionIndex: UInt): StartSectionVisitor = delegate?.visitStartSection(functionIndex) ?: StartSectionAdapter()

    override fun visitImportSection(): ImportSectionVisitor = delegate?.visitImportSection() ?: ImportSectionAdapter()

    override fun visitExportSection(): ExportSectionVisitor = delegate?.visitExportSection() ?: ExportSectionAdapter()

    override fun visitTableSection(): TableSectionVisitor = delegate?.visitTableSection() ?: TableSectionAdapter()

    override fun visitElementSection(): ElementSectionVisitor = delegate?.visitElementSection() ?: ElementSectionAdapter()

    override fun visitGlobalSection(): GlobalSectionVisitor = delegate?.visitGlobalSection() ?: GlobalSectionAdapter()

    override fun visitCodeSection(): CodeSectionVisitor = delegate?.visitCodeSection() ?: CodeSectionAdapter()

    override fun visitMemorySection(): MemorySectionVisitor = delegate?.visitMemorySection() ?: MemorySectionAdapter()

    override fun visitDataSection(): DataSectionVisitor = delegate?.visitDataSection() ?: DataSectionAdapter()

    override fun visitExceptionSection(): ExceptionSectionVisitor = delegate?.visitExceptionSection() ?: ExceptionSectionAdapter()

    override fun visitRelocationSection(): RelocationSectionVisitor = delegate?.visitRelocationSection() ?: RelocationSectionAdapter()

    override fun visitUnknownSection(name: String, content: ByteArray): UnknownSectionVisitor = delegate?.visitUnknownSection(name, content) ?: UnknownSectionAdapter()

    override fun visitLinkingSection(): LinkingSectionVisitor = delegate?.visitLinkingSection() ?: LinkingSectionAdapter()

    override fun visitNameSection(): NameSectionVisitor = delegate?.visitNameSection() ?: NameSectionAdapter()

    override fun visitDataCountSection(dataCount: UInt): DataCountSectionVisitor = delegate?.visitDataCountSection(dataCount) ?: DataCountSectionAdapter()

    override fun visitSourceMapSection(sourceMap: String): SourceMapSectionVisitor = delegate?.visitSourceMapSection(sourceMap) ?: SourceMapSectionAdapter()

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
