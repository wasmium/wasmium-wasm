package org.wasmium.wasm.binary.visitors

public interface ModuleVisitor {
    public fun visit(version: UInt)

    public fun visitTypeSection(): TypeSectionVisitor

    public fun visitFunctionSection(): FunctionSectionVisitor

    public fun visitStartSection(): StartSectionVisitor

    public fun visitImportSection(): ImportSectionVisitor

    public fun visitExportSection(): ExportSectionVisitor

    public fun visitTableSection(): TableSectionVisitor

    public fun visitElementSection(): ElementSectionVisitor

    public fun visitGlobalSection(): GlobalSectionVisitor

    public fun visitCodeSection(): CodeSectionVisitor

    public fun visitMemorySection(): MemorySectionVisitor

    public fun visitDataSection(): DataSectionVisitor

    public fun visitExceptionSection(): ExceptionSectionVisitor

    public fun visitRelocationSection(): RelocationSectionVisitor

    public fun visitCustomSection(): CustomSectionVisitor

    public fun visitLinkingSection(): LinkingSectionVisitor

    public fun visitNameSection(): NameSectionVisitor

    public fun visitDataCountSection(): DataCountSectionVisitor

    public fun visitEnd()
}
