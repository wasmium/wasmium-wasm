package org.wasmium.wasm.binary.visitors

public open class ModuleAdapter(protected val delegate: ModuleVisitor? = null) : ModuleVisitor {
    override fun visitHeader(version: UInt) {
        delegate?.visitHeader(version)
    }

    public override fun visitTypeSection(): TypeSectionVisitor? {
        if (delegate != null) {
            val typeSectionVisitor = delegate.visitTypeSection()
            if (typeSectionVisitor != null) {
                return TypeSectionAdapter(typeSectionVisitor)
            }
        }

        return null
    }


    public override fun visitFunctionSection(): FunctionSectionVisitor? {
        if (delegate != null) {
            val functionSectionVisitor = delegate.visitFunctionSection()
            if (functionSectionVisitor != null) {
                return FunctionSectionAdapter(functionSectionVisitor)
            }
        }

        return null
    }


    public override fun visitStartSection(functionIndex: UInt): StartSectionVisitor? {
        if (delegate != null) {
            val startSectionVisitor = delegate.visitStartSection(functionIndex)
            if (startSectionVisitor != null) {
                return StartSectionAdapter(startSectionVisitor)
            }
        }

        return null
    }

    public override fun visitImportSection(): ImportSectionVisitor? {
        if (delegate != null) {
            val importSectionVisitor = delegate.visitImportSection()
            if (importSectionVisitor != null) {
                return ImportSectionAdapter(importSectionVisitor)
            }
        }

        return null
    }

    public override fun visitExportSection(): ExportSectionVisitor? {
        if (delegate != null) {
            val exportSectionVisitor = delegate.visitExportSection()
            if (exportSectionVisitor != null) {
                return ExportSectionAdapter(exportSectionVisitor)
            }
        }

        return null
    }

    public override fun visitTableSection(): TableSectionVisitor? {
        if (delegate != null) {
            val tableSectionVisitor = delegate.visitTableSection()
            if (tableSectionVisitor != null) {
                return TableSectionAdapter(tableSectionVisitor)
            }
        }

        return null
    }

    public override fun visitElementSection(): ElementSectionVisitor? {
        if (delegate != null) {
            val elementSectionVisitor = delegate.visitElementSection()
            if (elementSectionVisitor != null) {
                return ElementSectionAdapter(elementSectionVisitor)
            }
        }

        return null
    }

    public override fun visitGlobalSection(): GlobalSectionVisitor? {
        if (delegate != null) {
            val globalSectionVisitor = delegate.visitGlobalSection()
            if (globalSectionVisitor != null) {
                return GlobalSectionAdapter(globalSectionVisitor)
            }
        }

        return null
    }

    public override fun visitCodeSection(): CodeSectionVisitor? {
        if (delegate != null) {
            val codeSectionVisitor = delegate.visitCodeSection()
            if (codeSectionVisitor != null) {
                return CodeSectionAdapter(codeSectionVisitor)
            }
        }

        return null
    }

    public override fun visitMemorySection(): MemorySectionVisitor? {
        if (delegate != null) {
            val memorySectionVisitor = delegate.visitMemorySection()
            if (memorySectionVisitor != null) {
                return MemorySectionAdapter(memorySectionVisitor)
            }
        }

        return null
    }

    public override fun visitDataSection(): DataSectionVisitor? {
        if (delegate != null) {
            val dataSectionVisitor = delegate.visitDataSection()
            if (dataSectionVisitor != null) {
                return DataSectionAdapter(dataSectionVisitor)
            }
        }

        return null
    }

    public override fun visitExceptionSection(): ExceptionSectionVisitor? {
        if (delegate != null) {
            val exceptionSectionVisitor = delegate.visitExceptionSection()
            if (exceptionSectionVisitor != null) {
                return ExceptionSectionAdapter(exceptionSectionVisitor)
            }
        }

        return null
    }

    public override fun visitRelocationSection(): RelocationSectionVisitor? {
        if (delegate != null) {
            val relocationSectionVisitor = delegate.visitRelocationSection()
            if (relocationSectionVisitor != null) {
                return RelocationSectionAdapter(relocationSectionVisitor)
            }
        }

        return null
    }

    public override fun visitUnknownSection(name: String, content: ByteArray): UnknownSectionVisitor? {
        if (delegate != null) {
            val customSectionVisitor = delegate.visitUnknownSection(name, content)
            if (customSectionVisitor != null) {
                return UnknownSectionAdapter(customSectionVisitor)
            }
        }

        return null
    }

    public override fun visitLinkingSection(): LinkingSectionVisitor? {
        if (delegate != null) {
            val linkingSectionVisitor = delegate.visitLinkingSection()
            if (linkingSectionVisitor != null) {
                return LinkingSectionAdapter(linkingSectionVisitor)
            }
        }

        return null
    }

    public override fun visitNameSection(): NameSectionVisitor? {
        if (delegate != null) {
            val nameSectionVisitor = delegate.visitNameSection()
            if (nameSectionVisitor != null) {
                return NameSectionAdapter(nameSectionVisitor)
            }
        }

        return null
    }

    public override fun visitDataCountSection(dataCount: UInt): DataCountSectionVisitor? {
        if (delegate != null) {
            val dataCountSectionVisitor = delegate.visitDataCountSection(dataCount)
            if (dataCountSectionVisitor != null) {
                return DataCountSectionAdapter(dataCountSectionVisitor)
            }
        }

        return null
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
