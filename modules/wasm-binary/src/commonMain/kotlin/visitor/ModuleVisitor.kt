package org.wasmium.wasm.binary.visitor

public interface ModuleVisitor {
    public fun visitHeader(version: UInt): Unit

    public fun visitTypeSection(): TypeSectionVisitor

    public fun visitFunctionSection(): FunctionSectionVisitor

    public fun visitStartSection(functionIndex: UInt): StartSectionVisitor

    public fun visitImportSection(): ImportSectionVisitor

    public fun visitExportSection(): ExportSectionVisitor

    public fun visitTableSection(): TableSectionVisitor

    public fun visitElementSection(): ElementSectionVisitor

    public fun visitGlobalSection(): GlobalSectionVisitor

    public fun visitCodeSection(): CodeSectionVisitor

    public fun visitMemorySection(): MemorySectionVisitor

    public fun visitDataSection(): DataSectionVisitor

    public fun visitRelocationSection(): RelocationSectionVisitor

    public fun visitUnknownSection(name: String, content: ByteArray): UnknownSectionVisitor

    public fun visitLinkingSection(): LinkingSectionVisitor

    public fun visitNameSection(): NameSectionVisitor

    public fun visitDataCountSection(dataCount: UInt): DataCountSectionVisitor

    public fun visitSourceMapSection(sourceMapUrl: String): SourceMapSectionVisitor

    public fun visitExternalDebugSection(externalDebugUrl: String): ExternalDebugSectionVisitor

    public fun visitTagSection(): TagSectionVisitor

    public fun visitEnd(): Unit
}
